import cats.effect.{IO, Sync}
import com.samdvr.consistenthashrouting.common._
import consul.Consul

import scala.concurrent.ExecutionContext

object ConsulRouter {
  def apply[N](consulClient: Consul)(implicit ec: ExecutionContext, sync: Sync[IO]): Router[IO, N] =
    Router(ConsulServiceDiscovery[IO, N](consulClient), Algorithm.jumpConsistentHash, sync)
}