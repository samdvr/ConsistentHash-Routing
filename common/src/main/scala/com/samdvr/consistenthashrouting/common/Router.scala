package com.samdvr.consistenthashrouting.common

import cats.data.EitherT
import cats.effect.Sync
import cats.implicits._

trait ServiceDiscovery[F[_], N] {
  def healthyNodes: F[List[N]]
}


trait Router[F[_], N] {
  def get(key: Int): EitherT[F, Throwable, N]
}

object Router {
  def apply[F[_], N](implicit s: ServiceDiscovery[F, N],
                     alg: Algorithm,
                     F: Sync[F]): Router[F, N] = (key: Int) => {
    val nodes: F[List[N]] = s.healthyNodes
    EitherT(nodes.map(fetchNode(key, _, alg)))
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


