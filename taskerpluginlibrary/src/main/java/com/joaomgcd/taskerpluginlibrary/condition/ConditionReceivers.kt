package com.joaomgcd.taskerpluginlibrary.condition

import android.app.IntentService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.joaomgcd.taskerpluginlibrary.TaskerPluginConstants
import com.joaomgcd.taskerpluginlibrary.extensions.startForegroundIfNeeded
import com.joaomgcd.taskerpluginlibrary.runner.IntentServiceParallel
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginRunner
import net.dinglisch.android.tasker.TaskerPlugin

private fun getAndHandleResult(context: Context?, intent: Intent?, resultBundle: Bundle, handler: (Int, Bundle?) -> Unit) {
    if (context == null) return
    val resultFromIntent = TaskerPluginRunnerCondition.getResultFromIntent(context, intent)
    if (context is IntentService && resultFromIntent?.hasStartedForeground != true) {
        TaskerPluginRunner.startForegroundIfNeeded(context)
    }
    var resultCode = TaskerPluginConstants.RESULT_CONDITION_UNKNOWN
    if (resultFromIntent != null) {
        resultCode = resultFromIntent.code
        resultBundle.putBundle(TaskerPluginConstants.VARIABLES, resultFromIntent.bundle)
    }
    handler(resultCode, resultBundle)
}

class BroadcastReceiverCondition : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        getAndHandleResult(context, intent, getResultExtras(true)) { resultCode, _ ->
            this.resultCode = resultCode
        }
    }
}

class IntentServiceCondition : IntentServiceParallel("IntentServiceTaskerCondition") {
    override fun onHandleIntent(intent: Intent) {
        startForegroundIfNeeded()
        val receiver = TaskerPlugin.Condition.getResultReceiver(intent) ?: return
        getAndHandleResult(this, intent, Bundle()) { resultCode, bundle ->
            receiver.send(resultCode, bundle)
        }

    }
}