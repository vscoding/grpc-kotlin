package io.intellij.kotlin.grpc.server.service

import io.grpc.stub.StreamObserver
import io.intellij.kotlin.grpc.api.HeartBeatServiceGrpc
import io.intellij.kotlin.grpc.api.common.Ping
import io.intellij.kotlin.grpc.api.common.Pong
import io.intellij.kotlin.grpc.commons.config.getLogger
import net.devh.boot.grpc.server.service.GrpcService

/**
 * HeartBeatService
 *
 * @author tech@intellij.io
 */
@GrpcService
class HeartBeatService : HeartBeatServiceGrpc.HeartBeatServiceImplBase() {
    companion object {
        private val log = getLogger(HeartBeatService::class.java)
    }

    override fun report(request: Ping, responseObserver: StreamObserver<Pong>) {
        val id: String = request.getId()
        log.debug("received heartbeat from {}", id)
        responseObserver.onNext(Pong.newBuilder().setRes("pong $id").build())
        responseObserver.onCompleted()
    }

}
