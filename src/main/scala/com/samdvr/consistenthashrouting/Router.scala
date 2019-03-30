package com.samdvr.consistenthashrouting

import com.samdvr.consistenthashrouting.algorithms.Algorithm
import cats.data.EitherT
import cats.effect._
import cats.implicits._
import consul.Consul
import com.samdvr.consistenthashrouting.serviceDiscovery.ServiceDiscovery

import scala.concurrent.ExecutionContext


trait Router[F[_]] {
  def get[N](key: Int): EitherT[F, Throwable, N]
}

object Router {
  def apply[F[_]](implicit s: ServiceDiscovery[F],
                  alg: Algorithm,
                  F: Sync[F]): Router[F] = new Router[F] {
    override def get[N](key: Int): EitherT[F, Throwable, N] = {
      val nodes = s.healthyNodes
      val result: F[Either[Throwable, N]] = nodes.map { n =>
        val count = n.size
        if (count > 0) {
          Right(alg(alg.get(key, count)))
        } else {
          Left(new Throwable("could not find any healthy node!"))
        }
      }
      EitherT(result)
    }
  }
}

object ConsistentHashConsul {
  def apply(consulClient: Consul)(implicit ec: ExecutionContext, sync: Sync[IO]): Router[IO] =
    Router(ServiceDiscovery.forConsul[IO](consulClient), Algorithm.jumpConsistentHash, sync)
}