package io.intellij.kotlin.grpc.client.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

/**
 * AsyncConfiguration
 *
 * @author tech@intellij.io
 */
@Configuration
@EnableScheduling
class AsyncConfiguration {
    @Bean
    fun taskExecutor(): TaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 10
        executor.maxPoolSize = 10
        executor.setWaitForTasksToCompleteOnShutdown(true)
        executor.setThreadNamePrefix("Async-")

        executor.initialize()
        return executor
    }

    @Bean
    fun taskScheduler(): TaskScheduler {
        val scheduler = ThreadPoolTaskScheduler()
        scheduler.setPoolSize(5)
        scheduler.setThreadNamePrefix("Task-")

        scheduler.initialize()
        return scheduler
    }
}
