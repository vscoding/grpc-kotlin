package io.intellij.kotlin.grpc.server.service

import io.grpc.stub.StreamObserver
import io.intellij.kotlin.grpc.api.HeartBeatProto
import io.intellij.kotlin.grpc.api.HeartBeatServiceGrpc
import io.intellij.kotlin.grpc.server.config.getLogger
import net.devh.boot.grpc.server.service.GrpcService

/**
 * HeartBeatService
 *
 * @author tech@intellij.io
 */
@GrpcService
class HeartBeatService : HeartBeatServiceGrpc.HeartBeatServiceImplBase() {
    private val log = getLogger(HeartBeatService::class.java)

    override fun report(request: HeartBeatProto.Ping, responseObserver: StreamObserver<HeartBeatProto.Pong>) {
        val id: String = request.getId()
        log.debug("received heartbeat from {}", id)
        responseObserver.onNext(
            HeartBeatProto.Pong.newBuilder().setRes("pong $id").build()
        )
        responseObserver.onCompleted()
    }

}
