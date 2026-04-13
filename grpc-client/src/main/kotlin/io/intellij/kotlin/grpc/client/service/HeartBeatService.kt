package io.intellij.kotlin.grpc.client.service

import io.grpc.StatusRuntimeException
import io.intellij.kotlin.grpc.api.HeartBeatServiceGrpc
import io.intellij.kotlin.grpc.api.common.Ping
import io.intellij.kotlin.grpc.client.config.GrpcConfig
import io.intellij.kotlin.grpc.client.context.RegistryService
import io.intellij.kotlin.grpc.commons.config.getLogger
import net.devh.boot.grpc.client.inject.GrpcClient
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

/**
 * HeartBeatService
 *
 * @author tech@intellij.io
 */
interface HeartBeatService {
  fun doHeartBeat(content: String)
}

@Service
class DefaultHeartBeatService(
  private val registryService: RegistryService,
) : HeartBeatService {
  companion object {
    private val log = getLogger(HeartBeatService::class.java)
  }

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
      val pong = heartBeatServiceBlockingStub
        .withDeadlineAfter(2, TimeUnit.SECONDS)
        .report(Ping.newBuilder().setId(content).build())
      log.debug("HeartBeat Down. Pong; Resp={}", pong.getRes())
      registryService.markServerReady()
    } catch (e: StatusRuntimeException) {
      registryService.markServerNotReady()
      log.debug("HeartBeat Down Failed. status={}", e.status.code)
    } catch (_: Exception) {
      registryService.markServerNotReady()
      log.debug("HeartBeat Down Failed !")
    }
  }
}
