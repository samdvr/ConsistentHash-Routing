package com.samdvr.consistenthashrouting.common

import cats.Applicative
import cats.effect.IO
import org.scalatest.{FunSpec, Matchers}

object TestServiceDiscovery extends ServiceDiscovery[IO, String] {
  override def healthyNodes: IO[List[String]] = IO(List("127.0.0.1", "127.0.0.2"))
}

class RouterTest extends FunSpec with Matchers {
  describe("Router") {
    implicit val al = Algorithm.jumpConsistentHash
    describe("get") {
      it("returns a healthy node given hash key") {
        implicit val s: ServiceDiscovery[IO, String] = TestServiceDiscovery
        val router = Router(s, al, Applicative[IO])
        assert(router.get(1).value.unsafeRunSync().right.get == "127.0.0.1")
        assert(router.get(4).value.unsafeRunSync().right.get == "127.0.0.2")
      }
    }

    describe("fetchNode") {
      describe("when healthy node exists") {
        it("returns node") {
          val node = Router.fetchNode(1, List("1"), al)
          assert(node.right.get == "1")
        }
      }

      describe("when there are no healthy nodes") {
        it("returns error") {
          val node = Router.fetchNode(1, List(), al)
          assert(node.left.get.getMessage.contains("Could not find any available nodes."))
        }
      }
    }
  }
}
