package io.intellij.kotlin.grpc.client.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import org.springframework.core.task.VirtualThreadTaskExecutor
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * AsyncConfiguration
 *
 * @author tech@intellij.io
 */
@EnableScheduling
@Configuration
class AsyncConfiguration {

    @Bean
    fun taskExecutor(): TaskExecutor {
        return VirtualThreadTaskExecutor("v-async-")
    }

}
