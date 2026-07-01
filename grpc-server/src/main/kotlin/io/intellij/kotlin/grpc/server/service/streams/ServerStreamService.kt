package io.intellij.kotlin.grpc.server.service.streams

import io.grpc.stub.ServerCallStreamObserver
import io.grpc.stub.StreamObserver
import io.intellij.kotlin.grpc.api.ServerStreamServiceGrpc
import io.intellij.kotlin.grpc.api.stream.StreamRequest
import io.intellij.kotlin.grpc.api.stream.StreamResponse
import io.intellij.kotlin.grpc.commons.config.getLogger
import net.devh.boot.grpc.server.service.GrpcService
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * ServerStreamService
 *
 * @author dev@intellij.io
 */
@GrpcService
class ServerStreamService : ServerStreamServiceGrpc.ServerStreamServiceImplBase() {
  companion object {
    private val log = getLogger(ServerStreamService::class.java)
  }

  /**
   * serverStreaming: 服务端流式发送数据给客户端
   */
  override fun serverStreaming(
    request: StreamRequest,
    responseObserver: StreamObserver<StreamResponse>,
  ) {
    val requestData = request.data
    log.info("[Server Stream] Receive Data From Client|requestData = {}", requestData)
    val serverObserver = responseObserver as? ServerCallStreamObserver<StreamResponse>
    if (serverObserver == null) {
      for (i in 1..10) {
        responseObserver.onNext(createResponse(requestData, i))
      }
      responseObserver.onCompleted()
      return
    }

    val next = AtomicInteger(1)
    val completed = AtomicBoolean(false)
    serverObserver.setOnCancelHandler {
      completed.set(true)
      log.debug("[Server Stream] Cancelled By Client|requestData = {}", requestData)
    }

    val drainLogic = {
      while (!completed.get() && serverObserver.isReady && next.get() <= 10) {
        serverObserver.onNext(createResponse(requestData, next.getAndIncrement()))
      }
      if (next.get() > 10 && completed.compareAndSet(false, true)) {
        serverObserver.onCompleted()
      }
    }

    serverObserver.setOnReadyHandler { drainLogic.invoke() }

    drainLogic.invoke()
    /*
    1. drainLogic.invoke() 第一次主动发送
    2. setOnReadyHandler(drain)：后面如果因为背压停住了，等 isReady 从 false 变回 true 时继续发送。 如果只写 drainLogic.invoke()，当 isReady 从 false 变回 true 时不会继续发送。
     */
  }

  private fun createResponse(requestData: String, index: Int): StreamResponse {
    return StreamResponse.newBuilder().setData(
      "Server Stream Response Data: $requestData-$index",
    ).build()
  }

}
