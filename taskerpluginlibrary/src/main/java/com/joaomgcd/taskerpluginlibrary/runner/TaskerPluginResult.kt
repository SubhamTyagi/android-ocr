package com.joaomgcd.taskerpluginlibrary.runner

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.joaomgcd.taskerpluginlibrary.output.runner.TaskerOutputForRunner
import com.joaomgcd.taskerpluginlibrary.output.runner.TaskerOutputsForRunner
import net.dinglisch.android.tasker.TaskerPlugin

class ArgsSignalFinish(val context: Context, val taskerIntent: Intent, val renames: TaskerOutputRenames? = null, val filter: ((TaskerOutputForRunner) -> Boolean)? = null)
sealed class TaskerPluginResult<TOutput>(val success: Boolean) {
    abstract fun signalFinish(args: ArgsSignalFinish): Boolean
}

class TaskerPluginResultSucess<TOutput>(val regular: TOutput? = null, val dynamic: TaskerOutputsForRunner? = null, val callbackUri: Uri? = null) : TaskerPluginResult<TOutput>(true) {
    override fun signalFinish(args: ArgsSignalFinish) = TaskerPlugin.Setting.signalFinish(args.context, args.taskerIntent, TaskerPlugin.Setting.RESULT_CODE_OK, TaskerOutputsForRunner.getVariableBundle(args.context, regular, dynamic, args.renames, args.filter), callbackUri)
}

class TaskerPluginResultError(code: Int, message: String) : TaskerPluginResultErrorWithOutput<Unit>(code, message) {
    constructor(t: Throwable) : this(t.hashCode(), t.message ?: t.toString())
}

open class TaskerPluginResultErrorWithOutput<TOutput>(private val code: Int, val message: String) : TaskerPluginResult<TOutput>(false) {
    override fun signalFinish(args: ArgsSignalFinish) =
        TaskerPlugin.Setting.signalFinish(args.context, args.taskerIntent, TaskerPlugin.Setting.RESULT_CODE_FAILED, Bundle().apply {
            putString("%err", code.toString())
            putString("%errmsg", message)
        })

    constructor(t: Throwable) : this(t.hashCode(), t.message ?: t.toString())
}


class TaskerPluginResultUnknown() : TaskerPluginResult<Unit>(false) {
    override fun signalFinish(args: ArgsSignalFinish) = false
}