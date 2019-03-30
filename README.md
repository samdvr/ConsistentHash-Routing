# Consistent Hashing Router

This library provides a consistent hashing algorithm
to route to different service discovery implementations.

Currently, supports consul with jump hash algorithm.

```scala
import com.samdvr.consistenthashrouting.consul._
val key = 123
val node = ConsulRouter(new consul.Consul(CONSUL_IP, CONSUL_PORT, Option(CONSUL_ACL_TOKEN))).get(key)
```