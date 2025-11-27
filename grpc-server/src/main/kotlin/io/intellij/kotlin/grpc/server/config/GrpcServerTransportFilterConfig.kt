package io.intellij.kotlin.grpc.server.config

import io.grpc.ServerBuilder
import io.grpc.ServerTransportFilter
import io.intellij.kotlin.grpc.commons.config.getLogger
import io.intellij.kotlin.grpc.server.context.RegistryService
import io.intellij.kotlin.grpc.server.filter.MonitoringServerTransportFilter
import net.devh.boot.grpc.server.serverfactory.GrpcServerConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * GrpcServerTransportFilterConfig
 *
 * @author tech@intellij.io
 */
@Configuration
class GrpcServerTransportFilterConfig {
    private val log = getLogger(GrpcServerTransportFilterConfig::class.java)

    @Bean
    fun monitoringServerTransportFilter(registryService: RegistryService): ServerTransportFilter {
        return MonitoringServerTransportFilter(registryService)
    }

    /**
     * 创建并注册一个 GrpcServerConfigurer，将给定的 ServerTransportFilter 添加到 Grpc Server 构建器。
     *
     * 链路参考：
     * [net.devh.boot.grpc.server.serverfactory.AbstractGrpcServerFactory.configure]
     * [net.devh.boot.grpc.server.autoconfigure.GrpcServerAutoConfiguration.compressionServerConfigurer]
     * [net.devh.boot.grpc.common.autoconfigure.GrpcCommonCodecAutoConfiguration.defaultCompressorRegistry]
     *
     * @param monitoringServerTransportFilter 要添加到 Grpc Server 的 ServerTransportFilter
     * @return 负责把过滤器加入 ServerBuilder 的 GrpcServerConfigurer
     */
    @Bean
    fun monitoringServerTransportFilterConfigurer(monitoringServerTransportFilter: ServerTransportFilter): GrpcServerConfigurer {
        log.info("GrpcServerConfigurer add MonitoringServerTransportFilter")
        return GrpcServerConfigurer { builder: ServerBuilder<*>? ->
            builder!!.addTransportFilter(
                monitoringServerTransportFilter
            )
        }
    }
}
