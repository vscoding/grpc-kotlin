package io.intellij.kotlin.grpc.client.service

import io.intellij.kotlin.grpc.api.HeartBeatProto
import io.intellij.kotlin.grpc.api.HeartBeatServiceGrpc
import io.intellij.kotlin.grpc.client.config.GrpcConfig
import io.intellij.kotlin.grpc.client.config.getLogger
import io.intellij.kotlin.grpc.client.context.RegistryOperator
import net.devh.boot.grpc.client.inject.GrpcClient
import org.springframework.stereotype.Service

/**
 * HeartBeatService
 *
 * @author tech@intellij.io
 */
interface HeartBeatService {
    fun doHeartBeat(content: String)

    @Service
    class DefaultHeartBeatService(
        private val registryOperator: RegistryOperator,
    ) : HeartBeatService {
        private val log = getLogger(DefaultHeartBeatService::class.java)

        @GrpcClient(GrpcConfig.GRPC_SERVER_INSTANCE)
        private lateinit var heartBeatServiceBlockingStub: HeartBeatServiceGrpc.HeartBeatServiceBlockingStub

        /**
         * Sends a HeartBeat report to the server and updates the server readiness status.
         *
         * GrpcConnectionClientInterceptor cancel
         */
        override fun doHeartBeat(content: String) {
            log.debug("HeartBeat Report. Ping. Content={}", content)
            try {
                val pong: HeartBeatProto.Pong = heartBeatServiceBlockingStub.report(
                    HeartBeatProto.Ping.newBuilder()
                        .setId(content)
                        .build()
                )
                log.debug("HeartBeat Down. Pong; Resp={}", pong.getRes())
                registryOperator.setServerReady()
            } catch (_: Exception) {
                // {@link io.intellij.kotlin.grpc.client.config.interceptor.GrpcConnectionClientInterceptor} cancel
                // sharedOperator.setServerNotReady();
                log.debug("HeartBeat Down Failed !")
            }
        }
    }

}
