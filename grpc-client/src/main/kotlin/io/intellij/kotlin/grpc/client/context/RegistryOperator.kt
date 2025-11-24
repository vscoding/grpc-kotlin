package io.intellij.kotlin.grpc.client.context

import io.intellij.kotlin.grpc.client.config.getLogger
import org.springframework.stereotype.Service
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.Volatile

/**
 * RegistryOperator
 *
 * @author tech@intellij.io
 */
interface RegistryOperator {
    /**
     * Sets the server to a ready state.
     */
    fun setServerReady()

    /**
     * Sets the server to a not ready state.
     */
    fun setServerNotReady()

    /**
     * Returns the status of the server readiness.
     *
     * @return `true` if the server is ready, `false` otherwise.
     */
    fun getServerReady(): Boolean

    /**
     * Sets the server connection details.
     *
     * @param remoteHost The host address of the remote server.
     * @param remotePort The port number of the remote server.
     * @param localPort  The port number of the local server.
     */
    fun setServerConn(remoteHost: String, remotePort: Int, localPort: Int)

    /**
     * Clears the server connection details.
     *
     *
     * This method clears the server connection details stored in the SharedOperator interface. After calling this
     * method, the server connection details will be reset to their default values.
     */
    fun clearServerConn()

    /**
     * Retrieves the server connection details.
     *
     * @return The ServerConn object representing the server connection details.
     */
    fun getSeverConn(): ServerConn

    /**
     * Connects to a remote host at the specified remote port, using the specified local port.
     *
     * @param remoteHost The host address of the remote server.
     * @param remotePort The port number of the remote server.
     * @param localPort  The port number of the local server.
     */
    fun connect(remoteHost: String, remotePort: Int, localPort: Int)

    /**
     * Disconnects from the currently connected server.
     *
     *
     * This method is used to disconnect from the server that was previously connected using the [.connect] method.
     * After calling this method, the connection to the server will be terminated and the server details will be reset to their default values.
     */
    fun disconnect()


    @Service
    class DefaultSharedOperator(
        private val severConnRegistry: ServerConnRegistry
    ) : RegistryOperator {

        private val log = getLogger(DefaultSharedOperator::class.java)

        @Volatile
        private var currentConn: ServerConn = ServerConn.DEFAULT

        private val connLock: Lock = ReentrantLock()

        override fun setServerReady() {
            severConnRegistry.serverReady.compareAndSet(false, true)
        }

        override fun setServerNotReady() {
            severConnRegistry.serverReady.compareAndSet(true, false)
        }


        override fun getServerReady(): Boolean {
            return severConnRegistry.serverReady.get()
        }

        override fun setServerConn(remoteHost: String, remotePort: Int, localPort: Int) {
            val serverConn: ServerConn = ServerConn.create(remoteHost, remotePort, localPort)
            if (severConnRegistry.serverConn.compareAndSet(currentConn, serverConn)) {
                log.debug("ServerConn {}", serverConn)
                currentConn = serverConn
            }
        }

        override fun clearServerConn() {
            if (severConnRegistry.serverConn.compareAndSet(currentConn, ServerConn.DEFAULT)) {
                log.debug("Clear ServerConn {}", currentConn)
                currentConn = ServerConn.DEFAULT
            }
        }

        override fun getSeverConn(): ServerConn {
            return currentConn
        }

        override fun connect(remoteHost: String, remotePort: Int, localPort: Int) {
            connLock.lock()
            try {
                this.setServerConn(remoteHost, remotePort, localPort)
                this.setServerReady()
            } finally {
                connLock.unlock()
            }
        }

        override fun disconnect() {
            connLock.lock()
            try {
                this.clearServerConn()
                this.setServerNotReady()
            } finally {
                connLock.unlock()
            }
        }
    }
}