package com.joaomgcd.taskerpluginlibrary.runner

import android.content.Context
import com.joaomgcd.taskerpluginlibrary.TaskerPluginConstants
import com.joaomgcd.taskerpluginlibrary.output.runner.TaskerOutputForRunner
import com.joaomgcd.taskerpluginlibrary.output.runner.TaskerOutputsForRunner

sealed class TaskerPluginResultCondition<TOutput>(val success: Boolean) {
    abstract val conditionResultCode: Int
}

class TaskerPluginResultConditionSatisfied<TOutput>(val context: Context, val regular: TOutput? = null, val dynamic: TaskerOutputsForRunner? = null) : TaskerPluginResultCondition<TOutput>(true) {
    override val conditionResultCode = TaskerPluginConstants.RESULT_CONDITION_SATISFIED
    fun getOutputBundle(renames: TaskerOutputRenames? = null, filter: ((TaskerOutputForRunner) -> Boolean)) = TaskerOutputsForRunner.getVariableBundle(context, regular, dynamic, renames, filter)
}

class TaskerPluginResultConditionUnsatisfied<TOutput>() : TaskerPluginResultCondition<TOutput>(false) {
    override val conditionResultCode = TaskerPluginConstants.RESULT_CONDITION_UNSATISFIED
}


class TaskerPluginResultConditionUnknown<TOutput>() : TaskerPluginResultCondition<TOutput>(false) {

    override val conditionResultCode = TaskerPluginConstants.RESULT_CONDITION_UNKNOWN
}