package com.samdvr.consistenthashrouting.common


trait Algorithm {
  def get(key: Int, nodeCount: Int): Int
}

object Algorithm {
  // Source: https://github.com/ssedano/jump-consistent-hash
  def jumpConsistentHash: Algorithm = new Algorithm {
    private val JUMP = 1L << 31

    private val CONSTANT = 2862933555777941757L

    override def get(key: Int, nodeCount: Int): Int = {
      var k = key.toLong
      var b: Double = -1
      var j: Double = 0

      while (j < nodeCount) {
        b = j
        k = k * CONSTANT + 1L
        j = (b + 1L) * (JUMP / ((k >>> 33) + 1L).toDouble)
      }
      b.toInt
    }

  }
}