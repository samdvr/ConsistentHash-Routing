package com.samdvr.consistenthashrouting.common

import cats.effect.{IO, Sync}
import org.scalatest.{FunSpec, Matchers}
import cats.implicits._

object TestServiceDiscovery extends ServiceDiscovery[IO, String] {
  override def healthyNodes: IO[List[String]] = IO(List("127.0.0.1", "127.0.0.2"))
}

class RouterTest extends FunSpec with Matchers {
  describe("Router") {
    describe("fetchNode") {
      it("returns a healthy node given hash key") {
        implicit val s: ServiceDiscovery[IO, String] = TestServiceDiscovery
        implicit val al = Algorithm.jumpConsistentHash
        val router = Router(s, al, Sync[IO])
        assert(router.get(1).value.unsafeRunSync().right.get == "127.0.0.1")
        assert(router.get(4).value.unsafeRunSync().right.get == "127.0.0.2")
      }
    }
  }
}
