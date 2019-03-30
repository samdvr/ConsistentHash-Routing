import cats.effect.{IO, Sync}
import com.samdvr.consistenthashrouting.common._
import consul.Consul
import consul.v1.common.Node
import scala.concurrent.ExecutionContext

object ConsulRouter {
  def apply[N](consulClient: Consul)(implicit ec: ExecutionContext, sync: Sync[IO]): Router[IO, Node] = {
    implicit val alg: Algorithm = Algorithm.jumpConsistentHash
    implicit val serviceDiscovery: ServiceDiscovery[IO, Node] = ConsulServiceDiscovery(consulClient)
    Router[IO, Node]
  }
}