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
 * @author tech@intellij.io
 */
@GrpcService
class ServerStreamService : ServerStreamServiceGrpc.ServerStreamServiceImplBase() {
  companion object {
    private val log = getLogger(ServerStreamService::class.java)
  }

  override fun serverStreaming(
    request: StreamRequest,
    responseObserver: StreamObserver<StreamResponse>,
  ) {
    val requestData = request.data
    log.info("[Server Stream] receive data: {}", requestData)
    val serverObserver = responseObserver as? ServerCallStreamObserver<StreamResponse>
    if (serverObserver == null) {
      for (i in 1..10) {
        responseObserver.onNext(response(requestData, i))
      }
      responseObserver.onCompleted()
      return
    }

    val next = AtomicInteger(1)
    val completed = AtomicBoolean(false)
    serverObserver.setOnCancelHandler {
      completed.set(true)
      log.debug("[Server Stream] cancelled by client. request={}", requestData)
    }
    val drain = Runnable {
      while (!completed.get() && serverObserver.isReady && next.get() <= 10) {
        serverObserver.onNext(response(requestData, next.getAndIncrement()))
      }
      if (next.get() > 10 && completed.compareAndSet(false, true)) {
        serverObserver.onCompleted()
      }
    }
    serverObserver.setOnReadyHandler(drain)
    drain.run()
  }

  private fun response(requestData: String, index: Int): StreamResponse {
    return StreamResponse.newBuilder().setData(
      "[Server Stream] response data: $requestData-$index",
    ).build()
  }

}
