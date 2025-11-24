package io.intellij.kotlin.grpc.client.config.interceptor

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.intellij.kotlin.grpc.client.config.getLogger
import io.intellij.kotlin.grpc.client.context.RegistryOperator
import java.util.Objects

/**
 * GrpcConnClientInterceptor
 *
 * @author tech@intellij.io
 */
class GrpcConnClientInterceptor(
    val registryOperator: RegistryOperator
) : ClientInterceptor {
    private val log = getLogger(GrpcConnClientInterceptor::class.java)

    override fun <ReqT, RespT> interceptCall(
        method: MethodDescriptor<ReqT?, RespT?>?,
        callOptions: CallOptions?, next: Channel
    ): ClientCall<ReqT, RespT> {

        return object : SimpleForwardingClientCall<ReqT, RespT>(next.newCall<ReqT, RespT>(method, callOptions)) {
            override fun start(responseListener: Listener<RespT?>?, headers: Metadata?) {
                // log.debug("GrpcConnectionClientInterceptor start");
                super.start(responseListener, headers)
            }

            override fun sendMessage(message: ReqT?) {
                // log.debug("GrpcConnectionClientInterceptor sendMessage");
                super.sendMessage(message)
            }

            override fun cancel(message: String?, cause: Throwable?) {
                try {
                    super.cancel(message, cause)
                } finally {
                    registryOperator.setServerNotReady()
                    log.error(
                        "GrpcConnectionClientInterceptor cancel {}",
                        if (Objects.nonNull(cause)) cause!!.javaClass else "NULL"
                    )
                }
            }
        }
    }
}
