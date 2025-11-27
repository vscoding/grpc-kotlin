package io.intellij.kotlin.grpc.commons.task

/**
 * CronTask
 *
 * @author tech@intellij.io
 */
interface CronTask {
    /**
     * Returns the cron expression for scheduling a task.
     *
     * @return the cron expression in the form of a string
     */
    fun cron(): String {
        return "*/5 * * * * ?"
    }

    /**
     * Checks if the task is currently running.
     *
     * @return true if the task is running, false otherwise
     */
    fun running(): Boolean

    /**
     * Starts the execution of the task.
     */
    fun start()

    /**
     * Stops the execution of the task.
     */
    fun stop()

}