package io.intellij.kotlin.grpc.client.context

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Service

/**
 * GrpcApplicationContext
 *
 * @author tech@intellij.io
 */
interface GrpcApplicationContext : ApplicationContextAware {
    /**
     * Retrieves the name of the application.
     *
     * @return The name of the application as a String.
     */
    val applicationName: String

    /**
     * Retrieves the Spring ApplicationContext.
     *
     * @return The Spring ApplicationContext.
     */
    val springApplicationContext: ApplicationContext?

    /**
     * Determines if the server is ready to accept requests.
     *
     * @return `true` if the server is ready, `false` otherwise
     */
    fun serverReady(): Boolean

    /**
     * Retrieves the server connection information.
     *
     * @return The server connection information as a ServerConn object.
     */
    fun serverConn(): ServerConn
}

@Service
class DefaultGrpcApplicationContextImpl(
    private val serverConnRegistry: ServerConnRegistry,
    @param:Value("\${spring.application.name}") private val appName: String
) : GrpcApplicationContext {

    private var applicationContext: ApplicationContext? = null

    override val applicationName: String
        get() = this.appName

    override val springApplicationContext: ApplicationContext?
        get() = this.applicationContext

    override fun serverReady(): Boolean = serverConnRegistry.serverReady.get()

    override fun serverConn(): ServerConn = serverConnRegistry.serverConn.get()

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }
}