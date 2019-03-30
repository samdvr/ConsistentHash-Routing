package com.samdvr.consistenthashrouting.serviceDiscovery

import cats.effect.Async
import consul.Consul

import scala.concurrent.ExecutionContext

trait ServiceDiscovery[F[_]] {
  def healthyNodes[N]: F[List[N]]
}

object ServiceDiscovery {
  def forConsul[F[_]](consulClient: Consul)(implicit F: Async[F], ec: ExecutionContext): ServiceDiscovery[F] = new ServiceDiscovery[F] {
    override def healthyNodes[N]: F[List[N]] = {
      import consulClient.v1._
      Async[F].async { cb =>
        import scala.util.{Failure, Success}
        catalog.nodes().onComplete {
          case Success(value) => cb(Right(value.toList))
          case Failure(error) => cb(Left(error))
        }
      }
    }
  }


}