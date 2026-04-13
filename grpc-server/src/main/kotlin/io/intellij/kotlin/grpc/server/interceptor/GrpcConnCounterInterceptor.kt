package io.intellij.kotlin.grpc.server.interceptor

import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import io.intellij.kotlin.grpc.commons.config.getLogger
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * GrpcConnCounterInterceptor
 *
 * @author tech@intellij.io
 */
class GrpcConnCounterInterceptor : ServerInterceptor {

  companion object {
    private val log = getLogger(GrpcConnCounterInterceptor::class.java)
  }

  private val activeRpcCount = AtomicInteger(0)

  override fun <ReqT, RespT> interceptCall(
    call: ServerCall<ReqT, RespT>,
    headers: Metadata?,
    next: ServerCallHandler<ReqT, RespT>,
  ): ServerCall.Listener<ReqT> {
    activeRpcCount.incrementAndGet()
    val methodDescriptor = call.getMethodDescriptor()
    val methodName = methodDescriptor.fullMethodName
    val closed = AtomicBoolean(false)

    log.debug("Active RPC Count: {}, method={}", activeRpcCount.get(), methodName)

    val listener = try {
      next.startCall(call, headers)
    } catch (t: Throwable) {
      closeRpc(closed, methodName)
      throw t
    }

    return object : SimpleForwardingServerCallListener<ReqT>(listener) {
      override fun onCancel() {
        try {
          log.debug("RPC cancelled by client. method={}", methodName)
          super.onCancel()
        } finally {
          closeRpc(closed, methodName)
        }
      }

      override fun onComplete() {
        try {
          log.debug("RPC completed by client. method={}", methodName)
          super.onComplete()
        } finally {
          closeRpc(closed, methodName)
        }
      }
    }
  }

  private fun closeRpc(closed: AtomicBoolean, methodName: String) {
    if (closed.compareAndSet(false, true)) {
      log.debug("Active RPC Count: {}, method={}", activeRpcCount.decrementAndGet(), methodName)
    }
  }

}
