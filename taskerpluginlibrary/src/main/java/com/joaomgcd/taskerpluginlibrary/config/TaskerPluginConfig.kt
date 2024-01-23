package com.joaomgcd.taskerpluginlibrary.config

import android.content.Context
import android.content.Intent
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput



interface TaskerPluginConfig<TInput : Any> {
    val context: Context
    fun finish()
    fun getIntent(): Intent?
    fun setResult(resultCode: Int, data: Intent)
    fun assignFromInput(input: TaskerInput<TInput>)
    val inputForTasker: TaskerInput<TInput>
}

interface TaskerPluginConfigNoInput : TaskerPluginConfig<Unit>{
    override fun assignFromInput(input: TaskerInput<Unit>) {}
    override val inputForTasker get() = TaskerInput(Unit)
}