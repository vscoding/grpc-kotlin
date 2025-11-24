package io.intellij.kotlin.grpc.client.config.aop

import io.intellij.kotlin.grpc.client.config.getLogger
import io.intellij.kotlin.grpc.client.context.GrpcApplicationContext
import io.intellij.kotlin.grpc.client.expection.GrpcServerNotReadyException
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component

/**
 * ServerReadyAdvice
 *
 * @author tech@intellij.io
 */
@Component
@Aspect
class RequireGrpcServerReadyAspect(
    private val grpcApplicationContext: GrpcApplicationContext
) {
    private val log = getLogger(RequireGrpcServerReadyAspect::class.java)

    @Pointcut("execution(* io.intellij.kotlin.grpc.client..*.*(..)) && @annotation(io.intellij.kotlin.grpc.client.config.anno.RequireGrpcServerReady)")
    fun pointCut() {
    }

    @Around("pointCut()")
    @Throws(Throwable::class)
    fun around(joinPoint: ProceedingJoinPoint): Any {
        if (grpcApplicationContext.serverReady()) {
            return joinPoint.proceed()
        } else {
            log.error(
                "Grpc Server Not Ready;AroundAdvice on {}#{}",
                joinPoint.target.javaClass.simpleName,
                joinPoint.signature.name
            )
            throw GrpcServerNotReadyException.create()
        }
    }
}
