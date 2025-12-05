package io.intellij.kotlin.grpc.server.service.streams

import io.grpc.stub.StreamObserver
import io.intellij.kotlin.grpc.api.ServerStreamServiceGrpc
import io.intellij.kotlin.grpc.api.stream.StreamRequest
import io.intellij.kotlin.grpc.api.stream.StreamResponse
import io.intellij.kotlin.grpc.commons.config.getLogger
import net.devh.boot.grpc.server.service.GrpcService
import java.lang.Thread.sleep

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
        responseObserver: StreamObserver<StreamResponse>
    ) {
        val requestData = request.data
        log.info("[Server Stream] receive data: {}", requestData)
        for (i in 1..10) {
            responseObserver.onNext(
                StreamResponse.newBuilder().setData(
                    "[Server Stream] response data: $requestData-$i"
                ).build()
            )
            sleep(100)
        }
        responseObserver.onCompleted()
    }

}