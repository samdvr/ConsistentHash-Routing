package com.samdvr.consistenthashrouting.common

import cats.effect.{IO, Sync}
import org.scalatest.{FunSpec, Matchers}

object TestServiceDiscovery extends ServiceDiscovery[IO, String] {
  override def healthyNodes: IO[List[String]] = IO(List("127.0.0.1", "127.0.0.2"))
}

class RouterTest extends FunSpec with Matchers {
  describe("Router") {
    describe("get") {
      it("returns a healthy node given hash key") {
        implicit val s: ServiceDiscovery[IO, String] = TestServiceDiscovery
        implicit val al = Algorithm.jumpConsistentHash
        val router = Router(s, al, Sync[IO])
        assert(router.get(1).value.unsafeRunSync().right.get == "127.0.0.1")
        assert(router.get(4).value.unsafeRunSync().right.get == "127.0.0.2")
      }
    }

    describe("fetchNode") {
      describe("when healthy node exists") {
        it("returns node") {
          val al = Algorithm.jumpConsistentHash
          val node = Router.fetchNode(1, List("1"), al)
          assert(node.right.get == "1")
        }
      }

      describe("when there are no healthy nodes") {
        it("returns error") {
          val al = Algorithm.jumpConsistentHash
          val node = Router.fetchNode(1, List(), al)
          assert(node.left.get.getMessage.contains("Could not find any available nodes."))
        }
      }
    }
  }
}
