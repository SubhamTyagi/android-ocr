package com.joaomgcd.taskerpluginlibrary.extensions

import android.annotation.TargetApi
import android.app.IntentService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.Bundle
import com.joaomgcd.taskerpluginlibrary.TaskerPluginConstants
import com.joaomgcd.taskerpluginlibrary.getForTaskerCompatibleInputTypes
import com.joaomgcd.taskerpluginlibrary.input.getInputFromTaskerIntent
import com.joaomgcd.taskerpluginlibrary.runner.IntentServiceParallel
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginRunner
import java.util.*


internal fun <T> ArrayList<T>?.addOrCreate(item: T) = (this ?: ArrayList()).apply { add(item) }

internal val Intent.taskerPluginExtraBundle: Bundle
    get() {
        var bundle = getBundleExtra(TaskerPluginConstants.EXTRA_BUNDLE)
        if (bundle == null) {
            bundle = Bundle()
            putExtra(TaskerPluginConstants.EXTRA_BUNDLE, bundle)
        }
        return bundle
    }
internal var Bundle.wasConfiguredBefore: Boolean
    get() = getBoolean(TaskerPluginConstants.EXTRA_WAS_CONFIGURED_BEFORE, false)
    set(value) = putBoolean(TaskerPluginConstants.EXTRA_WAS_CONFIGURED_BEFORE, value)
internal var Bundle.canBindFireService: Boolean
    get() = getBoolean(TaskerPluginConstants.EXTRA_CAN_BIND_FIRE_SETTING, false)
    set(value) = putBoolean(TaskerPluginConstants.EXTRA_CAN_BIND_FIRE_SETTING, value)
internal val Bundle.mayNeedToStartForeground get() = !canBindFireService
internal val Intent.mayNeedToStartForeground get() = extras?.mayNeedToStartForeground ?: false


internal var Bundle.runnerClass: String?
    get() = getString(TaskerPluginConstants.EXTRA_ACTION_RUNNER_CLASS, null)
    set(value) = putString(TaskerPluginConstants.EXTRA_ACTION_RUNNER_CLASS, value)
internal var Bundle.inputClass: String?
    get() = getString(TaskerPluginConstants.EXTRA_ACTION_INPUT_CLASS, null)
    set(value) = putString(TaskerPluginConstants.EXTRA_ACTION_INPUT_CLASS, value)

internal fun <TInput : Any> Intent?.getTaskerInput(context: Context, inputClass: Class<TInput>, defaultInput: TInput? = null) = getInputFromTaskerIntent(context, this, inputClass, defaultInput)

internal val Throwable.taskerErrorVariables: Bundle
    get() {
        val result = Bundle()
        result.putString("%err", hashCode().toString())
        result.putString("%errmsg", message)
        return result
    }


internal inline fun <reified TService : IntentServiceParallel> runFromTasker(context: Context?, intent: Intent?) {
    if (intent == null || context == null) return
    intent.component = ComponentName(context, TService::class.java)
    context.startServiceDependingOnTargetApi(intent)
}

@TargetApi(Build.VERSION_CODES.O)
internal fun Context.startServiceDependingOnTargetApi(intent: Intent): ComponentName? = if (hasToRunServicesInForeground) {
    startForegroundService(intent)
} else {
    startService(intent)
}

internal fun hasToRunServicesInForeground(targetSdkVersion: Int) = targetSdkVersion >= 26 && Build.VERSION.SDK_INT >= 26
internal val Context.currentTargetApi
    get() = applicationInfo?.targetSdkVersion ?: Build.VERSION.SDK_INT
internal val Context.hasToRunServicesInForeground get() = hasToRunServicesInForeground(currentTargetApi)
internal val ApplicationInfo.hasToRunServicesInForeground get() = hasToRunServicesInForeground(targetSdkVersion)
internal val Context.hasToRunForegroundServicesWithType get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && currentTargetApi >= 34

@TargetApi(Build.VERSION_CODES.O)
internal fun Context.startServiceDependingOnTargetApi(applicationInfo: ApplicationInfo, intent: Intent) = if (hasToRunServicesInForeground(applicationInfo.targetSdkVersion)) startForegroundService(intent) else startService(intent)

internal fun Bundle.putTaskerCompatibleInput(key: String, value: Any?): Boolean {
    return getForTaskerCompatibleInputTypes(value,
        { false },
        { putString(key, it); true },
        { putInt(key, it); true },
        { putLong(key, it); true },
        { putFloat(key, it); true },
        { putDouble(key, it); true },
        { putBoolean(key, it); true },
        { putStringArray(key, it); true },
        { putStringArrayList(key, it); true }
    )
}


internal fun IntentService.startForegroundIfNeeded() = TaskerPluginRunner.startForegroundIfNeeded(this)
val String.withUppercaseFirstLetter
    get() :String {
        if (this.isEmpty()) return this

        return substring(0, 1).uppercase() + substring(1)
    }

val String.splitWordsTitleCase
    get() = split("_").joinToString(" ") {
        it.withUppercaseFirstLetter
    }