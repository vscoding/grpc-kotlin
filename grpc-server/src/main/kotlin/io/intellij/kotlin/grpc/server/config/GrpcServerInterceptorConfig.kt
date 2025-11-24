package io.intellij.kotlin.grpc.server.config

import io.grpc.ServerInterceptor
import io.intellij.kotlin.grpc.server.interceptor.GrpcConnCounterInterceptor
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor
import org.springframework.context.annotation.Configuration

/**
 * GrpcServerInterceptorConfig
 *
 * @author tech@intellij.io
 */
@Configuration
class GrpcServerInterceptorConfig {

    @GrpcGlobalServerInterceptor
    fun connectionCountInterceptor(): ServerInterceptor {
        return GrpcConnCounterInterceptor()
    }

}