package io.intellij.kotlin.grpc.server.service.streams

import io.grpc.Status
import io.grpc.stub.ServerCallStreamObserver
import io.grpc.stub.StreamObserver
import io.intellij.kotlin.grpc.api.BidiStreamServiceGrpc
import io.intellij.kotlin.grpc.api.stream.StreamRequest
import io.intellij.kotlin.grpc.api.stream.StreamResponse
import io.intellij.kotlin.grpc.commons.config.getLogger
import net.devh.boot.grpc.server.service.GrpcService
import java.util.ArrayDeque
import java.util.concurrent.atomic.AtomicBoolean

/**
 * BidiStreamService
 *
 * @author tech@intellij.io
 */
@GrpcService
class BidiStreamService : BidiStreamServiceGrpc.BidiStreamServiceImplBase() {
  companion object {
    private val log = getLogger(BidiStreamService::class.java)
    private const val MAX_PENDING_RESPONSES = 16
  }

  override fun bidiStreaming(responseObserver: StreamObserver<StreamResponse>): StreamObserver<StreamRequest> {
    val serverObserver =
      responseObserver as? ServerCallStreamObserver<StreamResponse> ?: return simpleBidiStream(responseObserver)

    val pendingResponses = ArrayDeque<StreamResponse>()
    val pendingLock = Any()
    val inboundCompleted = AtomicBoolean(false)
    val closed = AtomicBoolean(false)

    fun drainPending() {
      synchronized(pendingLock) {
        while (!closed.get() && serverObserver.isReady && pendingResponses.isNotEmpty()) {
          serverObserver.onNext(pendingResponses.removeFirst())
        }
        if (!closed.get() && inboundCompleted.get() && pendingResponses.isEmpty()) {
          closed.set(true)
          serverObserver.onCompleted()
        }
      }
    }

    serverObserver.setOnCancelHandler {
      closed.set(true)
      synchronized(pendingLock) {
        pendingResponses.clear()
      }
      log.debug("[Bidi Stream] cancelled by client")
    }
    serverObserver.setOnReadyHandler { drainPending() }

    return object : StreamObserver<StreamRequest> {

      override fun onNext(request: StreamRequest) {
        val response = toResponse(request)
        var overflow = false
        synchronized(pendingLock) {
          if (closed.get()) {
            return
          }
          if (serverObserver.isReady && pendingResponses.isEmpty()) {
            serverObserver.onNext(response)
          } else if (pendingResponses.size < MAX_PENDING_RESPONSES) {
            pendingResponses.addLast(response)
          } else {
            overflow = true
          }
        }
        if (overflow && closed.compareAndSet(false, true)) {
          responseObserver.onError(
            Status.RESOURCE_EXHAUSTED
              .withDescription("bidi stream pending response limit exceeded")
              .asRuntimeException(),
          )
        }
      }

      override fun onError(t: Throwable) {
        closed.set(true)
        synchronized(pendingLock) {
          pendingResponses.clear()
        }
        log.error("Error occurred during bidi streaming", t)
      }

      override fun onCompleted() {
        log.info("[Bidi Stream] receive completed")
        inboundCompleted.set(true)
        drainPending()
      }
    }
  }

  private fun simpleBidiStream(responseObserver: StreamObserver<StreamResponse>): StreamObserver<StreamRequest> {
    return object : StreamObserver<StreamRequest> {
      override fun onNext(request: StreamRequest) {
        responseObserver.onNext(toResponse(request))
      }

      override fun onError(t: Throwable) {
        log.error("Error occurred during bidi streaming", t)
      }

      override fun onCompleted() {
        responseObserver.onCompleted()
      }
    }
  }

  private fun toResponse(request: StreamRequest): StreamResponse {
    return request.data.also {
      log.info("[Bidi Stream]receive data: {}", it)
    }.let {
      StreamResponse.newBuilder().setData("[Bidi Stream] Response data: $it").build()
    }
  }

}
