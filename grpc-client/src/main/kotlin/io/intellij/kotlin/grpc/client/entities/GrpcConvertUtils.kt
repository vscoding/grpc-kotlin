package io.intellij.kotlin.grpc.client.entities

import io.intellij.kotlin.grpc.multi.GreetResponse

/**
 * GrpcConvertUtils
 *
 * @author tech@intellij.io
 */
object GrpcConvertUtils {
    fun convert(req: GreetResponse): GreetResp {
        return GreetResp(req.msg)
    }
}
