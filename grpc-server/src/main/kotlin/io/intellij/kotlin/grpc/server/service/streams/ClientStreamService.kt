package io.intellij.kotlin.grpc.server.service.streams

import io.grpc.stub.StreamObserver
import io.intellij.kotlin.grpc.api.ClientStreamServiceGrpc
import io.intellij.kotlin.grpc.api.stream.StreamRequest
import io.intellij.kotlin.grpc.api.stream.StreamResponse
import io.intellij.kotlin.grpc.commons.config.getLogger
import net.devh.boot.grpc.server.service.GrpcService

/**
 * ClientStreamService
 *
 * @author tech@intellij.io
 */
@GrpcService
class ClientStreamService : ClientStreamServiceGrpc.ClientStreamServiceImplBase() {
  companion object {
    private val log = getLogger(ClientStreamService::class.java)
  }

  override fun clientStreaming(
    responseObserver: StreamObserver<StreamResponse>,
  ): StreamObserver<StreamRequest> {
    return object : StreamObserver<StreamRequest> {
      private var dataCount = 0

      override fun onNext(request: StreamRequest) {
        request.data.also {
          dataCount++
          log.info("[Client Stream] receive data: {}", it)
        }
      }

      override fun onError(t: Throwable) {
        log.error("Error occurred during client streaming", t)
      }

      override fun onCompleted() {
        log.info("[Client Stream] receive completed. data size = {}", dataCount)
        responseObserver.onNext(
          StreamResponse.newBuilder().setData(
            "[Client Stream] Response, total data size = $dataCount",
          ).build(),
        )
        responseObserver.onCompleted()
      }
    }
  }
}
