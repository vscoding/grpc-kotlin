package io.intellij.kotlin.grpc.client.context

import org.springframework.stereotype.Repository
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

/**
 * ServerConnRegistry
 *
 * @author tech@intellij.io
 */
@Repository
class ServerConnRegistry {
    private val _serverReady = AtomicBoolean(false)
    val serverReady: AtomicBoolean get() = _serverReady
    private val _serverConn = AtomicReference(ServerConn.DEFAULT)
    val serverConn: AtomicReference<ServerConn> get() = _serverConn
}