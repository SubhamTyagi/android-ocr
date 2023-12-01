package com.joaomgcd.taskerpluginlibrary.condition

import android.app.Activity
import android.app.IntentService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.joaomgcd.taskerpluginlibrary.NoEmptyConstructorException
import com.joaomgcd.taskerpluginlibrary.TaskerPluginConstants
import com.joaomgcd.taskerpluginlibrary.extensions.getTaskerInput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInputInfos
import com.joaomgcd.taskerpluginlibrary.output.runner.TaskerOutputForRunner
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultCondition
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultConditionSatisfied
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultConditionUnsatisfied
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginRunner
import net.dinglisch.android.tasker.TaskerPlugin


abstract class TaskerPluginRunnerConditionEvent<TInput : Any, TOutput : Any, TUpdate : Any>() : TaskerPluginRunnerCondition<TInput, TOutput, TUpdate>() {
    override val isEvent = true
}

abstract class TaskerPluginRunnerConditionState<TInput : Any, TOutput : Any>() : TaskerPluginRunnerCondition<TInput, TOutput, Unit>() {
    override val isEvent = false
}

abstract class TaskerPluginRunnerCondition<TInput : Any, TOutput : Any, TUpdate : Any>() : TaskerPluginRunner<TInput, TOutput>() {
    protected abstract val isEvent: Boolean
    private fun TaskerPluginResultCondition<TOutput>.getConditionResult(hasStartedForeground: Boolean, input: TaskerInput<TInput>? = null): TaskerPluginConditionResult {
        val bundle = if (this is TaskerPluginResultConditionSatisfied<TOutput>) {
            this.getOutputBundle(getRenames(context, input), { output: TaskerOutputForRunner -> shouldAddOutput(context, input, output) })
        } else {
            null
        }
        return TaskerPluginConditionResult(this.conditionResultCode, bundle, hasStartedForeground)
    }

    internal fun getResultFromIntent(context: Context?, taskerIntent: Intent?): TaskerPluginConditionResult {
        var hasStartedForeground = false
        try {
            if (context == null || taskerIntent == null) return TaskerPluginResultConditionUnsatisfied<TOutput>().getConditionResult(false)
            if (isEvent) {
                TaskerPlugin.Event.retrievePassThroughMessageID(taskerIntent).let { if (it == -1) return TaskerPluginResultConditionUnsatisfied<TOutput>().getConditionResult(false) }
            }
            if (context is IntentService) {
                context.startForegroundIfNeeded()
                hasStartedForeground = true
            }
            val input = taskerIntent.getTaskerInput(context, getInputClass(taskerIntent))
            val update = getUpdate<TUpdate>(context, taskerIntent)
            val satisfiedCondition = getSatisfiedCondition(context, input, update)
            return satisfiedCondition.getConditionResult(hasStartedForeground, input)
        } catch (t: Throwable) {
            t.printStackTrace()
            return TaskerPluginResultConditionUnsatisfied<TOutput>().getConditionResult(hasStartedForeground)
        }
    }

    abstract fun getSatisfiedCondition(context: Context, input: TaskerInput<TInput>, update: TUpdate?): TaskerPluginResultCondition<TOutput>

    private fun <TUpdate : Any> getUpdate(context: Context, taskerIntent: Intent): TUpdate? {
        val bundle = TaskerPlugin.Event.retrievePassThroughData(taskerIntent) ?: return null
        val updateClass = bundle.getString(TaskerPluginConstants.EXTRA_CONDITION_UPDATE_CLASS)
                ?: return null
        return try {
            val clazz = Class.forName(updateClass)
            val update = clazz?.newInstance() as TUpdate
            if (update === Unit) return null
            TaskerInputInfos.fromBundle(context, update, bundle)
            update
        } catch (t: InstantiationException) {
            throw NoEmptyConstructorException(updateClass)
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }
    }

    companion object {
        internal fun getResultFromIntent(context: Context?, taskerIntent: Intent?) = TaskerPluginRunner.getFromTaskerIntent<TaskerPluginRunnerCondition<*, *, *>>(taskerIntent)?.getResultFromIntent(context, taskerIntent)

        fun <TActivityClass : Activity> requestQuery(context: Context, configActivityClass: Class<TActivityClass>, update: Any? = null) {
            val intentRequest = Intent(TaskerPluginConstants.ACTION_REQUEST_QUERY).apply {
                addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
                putExtra(TaskerPluginConstants.EXTRA_ACTIVITY, configActivityClass.name)
                TaskerPlugin.Event.addPassThroughMessageID(this)
                update.getUpdateBundle(context)?.let { TaskerPlugin.Event.addPassThroughData(this, it) }
            }
            val packagesAlreadyHandled = try {
                requestQueryThroughServicesAndGetSuccessPackages(context, intentRequest)
            } catch (ex: Exception) {
                listOf<String>()
            }
            requestQueryThroughBroadcasts(context, intentRequest, packagesAlreadyHandled)

        }

        private fun requestQueryThroughServicesAndGetSuccessPackages(context: Context, intentRequest: Intent): List<String> {
            val packageManager = context.getPackageManager()
            val intent = Intent(TaskerPluginConstants.ACTION_REQUEST_QUERY)
            val resolveInfos = packageManager.queryIntentServices(intent, 0)
            val result = arrayListOf<String>()
            resolveInfos.forEach { resolveInfo ->
                val serviceInfo = resolveInfo.serviceInfo
                val applicationInfo = serviceInfo.applicationInfo
                val componentName = ComponentName(serviceInfo.packageName, serviceInfo.name)
                intentRequest.component = componentName
                try{
                    context.startService(intentRequest)
                    result.add(applicationInfo.packageName)
                }catch (t:Throwable){
                    //not successful. Don't add to successes
                }
            }
            return result
        }

        private fun requestQueryThroughBroadcasts(context: Context, intentRequest: Intent, ignorePackages: List<String>) {
            if (ignorePackages.isEmpty()) {
                context.sendBroadcast(intentRequest)
                return
            }
            val packageManager = context.getPackageManager()
            val intent = Intent(TaskerPluginConstants.ACTION_REQUEST_QUERY)
            val resolveInfos = packageManager.queryBroadcastReceivers(intent, 0)
            return resolveInfos.forEach { resolveInfo ->
                val broadcastInfo = resolveInfo.activityInfo
                val applicationInfo = broadcastInfo.applicationInfo
                if (ignorePackages.contains(applicationInfo.packageName)) return@forEach

                val componentName = ComponentName(broadcastInfo.packageName, broadcastInfo.name)
                intentRequest.component = componentName
                context.sendBroadcast(intentRequest)
            }
        }

        private fun Any?.getUpdateBundle(context: Context) = this?.let { update ->
            val bundle = TaskerInputInfos.fromInput(context, update).bundle
            bundle.putString(TaskerPluginConstants.EXTRA_CONDITION_UPDATE_CLASS, update::class.java.name)
            bundle
        }
    }
}