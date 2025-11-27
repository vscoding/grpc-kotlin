package io.intellij.kotlin.grpc.commons.task

import org.springframework.beans.factory.InitializingBean
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.support.CronTrigger
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.atomic.AtomicBoolean

/**
 * AbstractCronTask
 *
 * @author tech@intellij.io
 */
abstract class AbstractCronTask : CronTask, InitializingBean {

    private val status = AtomicBoolean(false)

    private var future: ScheduledFuture<*>? = null

    override fun running(): Boolean {
        return status.get()
    }

    /**
     * Retrieves the TaskScheduler for scheduling tasks.
     *
     * @return The TaskScheduler instance used for task scheduling.
     */
    abstract fun getTaskScheduler(): TaskScheduler?

    /**
     * Retrieves the [Runnable] instance for executing the task.
     *
     * @return The [Runnable] instance for executing the task.
     */
    abstract fun getRunnable(): Runnable?

    /**
     * Determines whether the implementing class should start running the task on initialization.
     *
     * @return true if the task should start on initialization, false otherwise.
     */
    open fun startOnInitializing(): Boolean {
        return false
    }

    override fun start() {
        if (!status.compareAndSet(false, true)) {
            return
        }
        getRunnable()?.let {
            future = getTaskScheduler()?.schedule(it, CronTrigger(cron()))
        }
    }

    override fun stop() {
        if (!status.compareAndSet(true, false)) {
            return
        }
        if (future != null) {
            future!!.cancel(true)
        }
    }

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        if (startOnInitializing()) {
            start()
        }
    }

}