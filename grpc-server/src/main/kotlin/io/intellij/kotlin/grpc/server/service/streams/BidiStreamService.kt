package io.intellij.kotlin.grpc.server.service.streams

import io.grpc.stub.StreamObserver
import io.intellij.kotlin.grpc.api.BidiStreamServiceGrpc
import io.intellij.kotlin.grpc.api.stream.StreamRequest
import io.intellij.kotlin.grpc.api.stream.StreamResponse
import io.intellij.kotlin.grpc.commons.config.getLogger
import net.devh.boot.grpc.server.service.GrpcService

/**
 * BidiStreamService
 *
 * @author tech@intellij.io
 */
@GrpcService
class BidiStreamService : BidiStreamServiceGrpc.BidiStreamServiceImplBase() {
    private val log = getLogger(BidiStreamService::class.java)

    override fun bidiStreaming(responseObserver: StreamObserver<StreamResponse>): StreamObserver<StreamRequest> {

        return object : StreamObserver<StreamRequest> {

            override fun onNext(request: StreamRequest) {
                // echo back
                request.data.let {
                    log.info("[Bidi Stream]receive data: {}", it)
                    StreamResponse.newBuilder().setData("[Bidi Stream] Response data: $it").build()
                        .let { response -> responseObserver.onNext(response) }
                }
            }

            override fun onError(t: Throwable) {
                log.error("Error occurred during bidi streaming", t)
            }

            override fun onCompleted() {
                log.info("[Bidi Stream] receive completed")
                responseObserver.onCompleted()
            }
        }
    }

}