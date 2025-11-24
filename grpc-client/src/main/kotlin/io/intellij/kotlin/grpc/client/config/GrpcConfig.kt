package io.intellij.kotlin.grpc.client.config

import io.grpc.ClientInterceptor
import io.grpc.ClientTransportFilter
import io.grpc.ManagedChannelBuilder
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import io.intellij.kotlin.grpc.client.config.filter.MonitoringClientTransportFilter
import io.intellij.kotlin.grpc.client.config.interceptor.GrpcConnectionClientInterceptor
import io.intellij.kotlin.grpc.client.context.RegistryOperator
import net.devh.boot.grpc.client.channelfactory.GrpcChannelConfigurer
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * GrpcConfig
 *
 * @author tech@intellij.io
 */
@Configuration
class GrpcConfig {
    @Bean
    fun monitoringClientTransportFilter(registryOperator: RegistryOperator): ClientTransportFilter {
        return MonitoringClientTransportFilter(registryOperator)
    }

    /**
     * Adds a client transport filter to the channel builder, if it is an instance of NettyChannelBuilder.
     *
     * [GrpcChannelConfigurer](https://yidongnan.github.io/grpc-spring-boot-starter/zh-CN/client/configuration.html#clientInterceptor)
     *
     * @param monitoringClientTransportFilter the client transport filter to be added
     * @return the configured channel builder
     */
    @Bean
    fun transportFilter(monitoringClientTransportFilter: ClientTransportFilter?): GrpcChannelConfigurer {
        return GrpcChannelConfigurer { channelBuilder: ManagedChannelBuilder<*>?, name: String? ->
            if (channelBuilder is NettyChannelBuilder) {
                channelBuilder // .enableRetry()
                    // .maxHedgedAttempts(10)
                    .addTransportFilter(monitoringClientTransportFilter)
            }
        }
    }

    @GrpcGlobalClientInterceptor
    fun grpcConnectionClientInterceptor(registryOperator: RegistryOperator): ClientInterceptor {
        return GrpcConnectionClientInterceptor(registryOperator)
    }

    companion object {
        const val GRPC_SERVER_INSTANCE: String = "grpc-server"
    }

}
