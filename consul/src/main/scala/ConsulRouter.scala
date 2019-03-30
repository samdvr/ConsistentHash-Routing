import cats.effect.{IO, Sync}
import com.samdvr.consistenthashrouting.common._
import consul.Consul

import scala.concurrent.ExecutionContext

object ConsulRouter {
  def apply(consulClient: Consul)(implicit ec: ExecutionContext, sync: Sync[IO]): Router[IO] =
    Router(ConsulServiceDiscovery[IO](consulClient), Algorithm.jumpConsistentHash, sync)
}