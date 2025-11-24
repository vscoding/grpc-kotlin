package io.intellij.kotlin.grpc.client.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import org.springframework.core.task.VirtualThreadTaskExecutor
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

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

    @Bean
    fun taskScheduler(): TaskScheduler {
        val scheduler = ThreadPoolTaskScheduler()
        scheduler.setPoolSize(4)
        scheduler.setThreadNamePrefix("Task-")

        scheduler.initialize()
        return scheduler
    }

}
