import cats.effect.Async
import com.samdvr.consistenthashrouting.common.ServiceDiscovery
import consul.Consul

import scala.concurrent.ExecutionContext

object ConsulServiceDiscovery {
  def apply[F[_]](consulClient: Consul)(implicit F: Async[F],
                                        ec: ExecutionContext): ServiceDiscovery[F] = new ServiceDiscovery[F] {
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