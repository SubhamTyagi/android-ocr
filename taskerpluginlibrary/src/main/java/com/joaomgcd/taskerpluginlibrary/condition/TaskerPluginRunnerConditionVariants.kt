package com.joaomgcd.taskerpluginlibrary.condition

import android.content.Context
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultCondition
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultConditionSatisfied


abstract class TaskerPluginRunnerConditionNoOutput<TInput : Any, TUpdate : Any>() : TaskerPluginRunnerCondition<TInput, Unit, TUpdate>()
abstract class TaskerPluginRunnerConditionNoInput<TOutput : Any, TUpdate : Any>() : TaskerPluginRunnerCondition<Unit, TOutput, TUpdate>()
abstract class TaskerPluginRunnerConditionNoOutputOrInput<TUpdate : Any>() : TaskerPluginRunnerConditionNoOutput<Unit, TUpdate>()
abstract class TaskerPluginRunnerConditionNoOutputOrInputOrUpdate() : TaskerPluginRunnerConditionNoOutputOrInput<Unit>()

class TaskerPluginRunnerConditionNoOutputOrInputOrUpdateEvent() : TaskerPluginRunnerConditionNoOutputOrInput<Unit>() {
    override val isEvent: Boolean get() = true

    override fun getSatisfiedCondition(context: Context, input: TaskerInput<Unit>, update: Unit?): TaskerPluginResultCondition<Unit> {
        return TaskerPluginResultConditionSatisfied(context)
    }
}

abstract class TaskerPluginRunnerConditionNoOutputOrInputOrUpdateState() : TaskerPluginRunnerConditionNoOutputOrInput<Unit>() {
    override val isEvent: Boolean
        get() = false
}