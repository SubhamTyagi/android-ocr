package com.joaomgcd.taskerpluginlibrary.action

import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginRunner


abstract class TaskerPluginRunnerActionNoOutput<TInput : Any>() : TaskerPluginRunnerAction<TInput, Unit>()
abstract class TaskerPluginRunnerActionNoInput<TOutput : Any>() : TaskerPluginRunnerAction<Unit,TOutput>()
abstract class TaskerPluginRunnerActionNoOutputOrInput() : TaskerPluginRunnerActionNoOutput<Unit>()