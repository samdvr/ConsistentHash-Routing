package com.samdvr.consistenthashrouting.common

import cats.Applicative
import cats.data.EitherT
import cats.implicits._

import scala.util.hashing.MurmurHash3

trait ServiceDiscovery[F[_], N] {
  def healthyNodes: F[List[N]]
}


trait Router[F[_], N] {
  def get(key: Int): EitherT[F, Throwable, N]
}

object Router {
  def apply[F[_], N](implicit s: ServiceDiscovery[F, N],
                     alg: Algorithm,
                     F: Applicative[F]): Router[F, N] = (key: Int) => {
    EitherT(s.healthyNodes.map(fetchNode(key, _, alg)))
  }

  private[common] def fetchNode[N](key: Int, nodes: List[N], alg: Algorithm): Either[Throwable, N] = {
    val count = nodes.size
    if ((count - 1) > -1) {
      val index = alg.get(key, count)
      if (count >= index) {
        Right(nodes(index))
      } else {
        Left(new Throwable("Returned index is greater than nodes available."))
      }
    } else {
      Left(new Throwable("Could not find any available nodes."))
    }
  }

}

trait RouterOps {

  implicit class Ops[F[_], N](r: Router[F, N]) {
    def get(key: String): EitherT[F, Throwable, N] = r.get(MurmurHash3.stringHash(key))
  }

}

object implicits extends RouterOps
