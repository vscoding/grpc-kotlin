package io.intellij.kotlin.grpc.server.interceptor

import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import io.intellij.kotlin.grpc.server.config.getLogger
import java.util.concurrent.atomic.AtomicInteger

/**
 * GrpcConnectionCountInterceptor
 *
 * @author tech@intellij.io
 */
class GrpcConnectionCountInterceptor : ServerInterceptor {
    private val log = getLogger(GrpcConnectionCountInterceptor::class.java)
    private val connectionCount = AtomicInteger(0)

    override fun <ReqT, RespT> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata?,
        next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {
        connectionCount.incrementAndGet()

        log.debug("Current Connection Count: {}", connectionCount.get())
        val methodDescriptor = call.getMethodDescriptor()

        log.debug("MethodDescriptor | {}", methodDescriptor.fullMethodName)

        return object : SimpleForwardingServerCallListener<ReqT>(next.startCall(call, headers)) {
            override fun onHalfClose() {
                try {
                    super.onHalfClose()
                } finally {
                    connectionCount.decrementAndGet()
                    log.debug("Current Connection Count: {}", connectionCount.get())
                }
            }

            override fun onCancel() {
                log.debug("Connection cancelled by client.")
                super.onCancel()
            }

            override fun onComplete() {
                log.debug("Connection completed by client.")
                super.onComplete()
            }
        }
    }
}