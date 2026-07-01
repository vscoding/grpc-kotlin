package io.intellij.kotlin.grpc.server.context

import com.google.common.collect.Maps
import io.intellij.kotlin.grpc.context.NetworkAddr
import org.springframework.stereotype.Repository
import java.util.ArrayDeque

/**
 * ClientConnRegistry
 *
 * @author dev@intellij.io
 */
@Repository
class ClientConnStateRuntime {
  private val _live: MutableMap<NetworkAddr, ClientConnState> = Maps.newConcurrentMap()
  val live: MutableMap<NetworkAddr, ClientConnState> get() = _live

  private val _history: ArrayDeque<ClientConnState> = ArrayDeque()
  val history: ArrayDeque<ClientConnState> get() = _history

  fun clearHistoryClients() = _history.clear()

}
