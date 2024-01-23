package com.joaomgcd.taskerpluginlibrary.action

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.joaomgcd.taskerpluginlibrary.extensions.mayNeedToStartForeground
import com.joaomgcd.taskerpluginlibrary.extensions.runFromTasker
import com.joaomgcd.taskerpluginlibrary.runner.IntentServiceParallel
import net.dinglisch.android.tasker.TaskerPlugin


class BroadcastReceiverAction : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        resultCode = TaskerPlugin.Setting.RESULT_CODE_PENDING
        try {
            runFromTasker<IntentServiceAction>(context, intent)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}

class IntentServiceAction : IntentServiceParallel("IntentServiceTaskerAction") {
    override fun onHandleIntent(intent: Intent) {
        val mayNeedToStartForeground: Boolean = intent.mayNeedToStartForeground
        startForegroundIfNeeded(mayNeedToStartForeground)
        val result = TaskerPluginRunnerAction.runFromIntent(this, intent)
        if (!result.hasStartedForeground) {
            startForegroundIfNeeded(mayNeedToStartForeground)
        }
    }
}