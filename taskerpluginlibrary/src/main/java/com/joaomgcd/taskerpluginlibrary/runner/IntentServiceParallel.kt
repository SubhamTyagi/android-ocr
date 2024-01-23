package com.joaomgcd.taskerpluginlibrary.runner

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

/**
 * A drop-in replacement for IntentService that performs various tasks at the same time instead of one after the other
 *
 * @property name Name for the service. Will be used to name the threads created in this service.
 */
abstract class IntentServiceParallel(val name: String) : Service() {
    /**
     * Simply call [onStart] like [IntentService] does
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        onStart(intent, startId)
        return Service.START_NOT_STICKY
    }

    private val binder by lazy { Binder() }
    fun startForegroundIfNeeded(mayNeedToStartForeground: Boolean = true) = TaskerPluginRunner.startForegroundIfNeeded(this, mayNeedToStartForeground = mayNeedToStartForeground)
    override fun onBind(intent: Intent) = binder
    protected abstract fun onHandleIntent(intent: Intent)

    /**
     * Use a handler on the main thread to post exceptions and stop the service when all tasks are done
     */
    private val handler = Handler(Looper.getMainLooper())

    /**
     * Keep count of how many tasks are active so that we can stop when they reach 0
     */
    private var jobsCount: AtomicInteger = AtomicInteger(0)

    /**
     *
     * The executor to run tasks in parallel threads
     *
     */
    private val executor by lazy { Executors.newCachedThreadPool { runnable -> Thread(runnable, "IntentServiceParallel$name") } }

    /**
     * Keep track of the last startId sent to the service. Will be used to make sure we only stop the service if the last startId was actually the last startId that the service received.
     */
    private var lastStartId: Int? = null

    /**
     * Main function of the class. Starts processing each new task in parallel with existing tasks. When all tasks are processed will stop itself. Will ignore null intents.
     */
    override fun onStart(intent: Intent?, startId: Int) {
        if (intent == null) return

        //Count +1 so that we know how many tasks are running
        jobsCount.addAndGet(1)

        /**
         * store the startId so that we will always use [stopSelf] with the correct Id.
         * This is stored after incrementing the count so that if [stopSelf] runs before
         * the increment the service is not stopped because the last startId is not used as a parameter
         */
        lastStartId = startId
        executor.submit {
            try {
                //run task in parallel
                onHandleIntent(intent)
            } catch (throwable: RuntimeException) {
                //throw any exception in the main thread
                handler.post { throw throwable }
            } finally {
                handler.post {
                    //decrement in the main thread to avoid concurrency issues
                    if (jobsCount.decrementAndGet() > 0) return@post

                    //stop only if lastStartId was the last startId that was posted
                    lastStartId.let { if (it != null) stopSelf(it) else stopSelf() }
                }
            }
        }

    }


    /**
     * Shutdown the executor when the sevice is destroyed
     */
    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }
}