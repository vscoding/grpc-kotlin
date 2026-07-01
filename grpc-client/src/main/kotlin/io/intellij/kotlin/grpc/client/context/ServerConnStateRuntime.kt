package io.intellij.kotlin.grpc.client.context

import org.springframework.stereotype.Repository
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

/**
 * ServerConnRuntime
 *
 * @author dev@intellij.io
 */
@Repository
class ServerConnStateRuntime {
  private val _serverReady = AtomicBoolean(false)
  val serverReady: AtomicBoolean get() = _serverReady
  private val _serverConnState = AtomicReference(ServerConnState.DEFAULT)
  val serverConnState: AtomicReference<ServerConnState> get() = _serverConnState
}