package com.joaomgcd.taskerpluginlibrary.config

import com.joaomgcd.taskerpluginlibrary.condition.*
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginRunner


abstract class TaskerPluginConfigHelperNoOutput<TInput : Any, TActionRunner : TaskerPluginRunner<TInput, Unit>>(config: TaskerPluginConfig<TInput>) : TaskerPluginConfigHelper<TInput, Unit, TActionRunner>(config) {
    override val outputClass = Unit::class.java
}

abstract class TaskerPluginConfigHelperNoInput<TOutput : Any, TActionRunner : TaskerPluginRunner<Unit, TOutput>>(config: TaskerPluginConfig<Unit>) : TaskerPluginConfigHelper<Unit, TOutput, TActionRunner>(config) {
    override val inputClass = Unit::class.java
}

abstract class TaskerPluginConfigHelperNoOutputOrInput<TActionRunner : TaskerPluginRunner<Unit, Unit>>(config: TaskerPluginConfig<Unit>) : TaskerPluginConfigHelperNoOutput<Unit, TActionRunner>(config) {
    override val inputClass = Unit::class.java
    override val outputClass = Unit::class.java
}

abstract class TaskerPluginConfigHelperConditionNoOutput<TInput : Any, TUpdate : Any, TActionRunner : TaskerPluginRunnerConditionNoOutput<TInput, TUpdate>>(config: TaskerPluginConfig<TInput>) : TaskerPluginConfigHelper<TInput, Unit, TActionRunner>(config) {
    override val outputClass = Unit::class.java
}

abstract class TaskerPluginConfigHelperConditionNoInput<TOutput : Any, TUpdate : Any, TActionRunner : TaskerPluginRunnerConditionNoInput<TOutput, TUpdate>>(config: TaskerPluginConfig<Unit>) : TaskerPluginConfigHelper<Unit, TOutput, TActionRunner>(config) {
    override val inputClass = Unit::class.java
}

abstract class TaskerPluginConfigHelperConditionNoOutputOrInput<TUpdate : Any, TActionRunner : TaskerPluginRunnerConditionNoOutputOrInput<TUpdate>>(config: TaskerPluginConfig<Unit>) : TaskerPluginConfigHelperNoOutputOrInput<TActionRunner>(config) {
    override val inputClass = Unit::class.java
    override val outputClass = Unit::class.java
}

abstract class TaskerPluginConfigHelperConditionNoOutputOrInputOrUpdate(config: TaskerPluginConfig<Unit>) : TaskerPluginConfigHelperNoOutputOrInput<TaskerPluginRunnerConditionNoOutputOrInputOrUpdate>(config) {
    override val inputClass = Unit::class.java
    override val outputClass = Unit::class.java
}

open class TaskerPluginConfigHelperEventNoOutputOrInputOrUpdate(config: TaskerPluginConfig<Unit>) : TaskerPluginConfigHelperNoOutputOrInput<TaskerPluginRunnerConditionNoOutputOrInputOrUpdateEvent>(config) {
    override val runnerClass get() = TaskerPluginRunnerConditionNoOutputOrInputOrUpdateEvent::class.java
    override val inputClass = Unit::class.java
    override val outputClass = Unit::class.java
}
abstract class TaskerPluginConfigHelperStateNoOutputOrInputOrUpdate<TRunner:TaskerPluginRunnerConditionNoOutputOrInputOrUpdateState>(config: TaskerPluginConfig<Unit>) : TaskerPluginConfigHelperNoOutputOrInput<TRunner>(config) {
    override val inputClass = Unit::class.java
    override val outputClass = Unit::class.java
}
