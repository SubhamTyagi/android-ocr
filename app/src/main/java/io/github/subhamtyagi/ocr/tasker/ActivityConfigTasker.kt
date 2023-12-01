package io.github.subhamtyagi.ocr.tasker

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.joaomgcd.taskerpluginlibrary.SimpleResultError
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelper
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginRunner
abstract class ActivityConfigTasker<TInput : Any, TOutput : Any, TActionRunner : TaskerPluginRunner<TInput, TOutput>, THelper : TaskerPluginConfigHelper<TInput, TOutput, TActionRunner>, TBinding : ViewBinding> : Activity(), TaskerPluginConfig<TInput> {
    abstract fun getNewHelper(config: TaskerPluginConfig<TInput>): THelper
    protected abstract fun inflateBinding(layoutInflater: LayoutInflater): TBinding?

    protected var binding: TBinding? = null

    protected val taskerHelper by lazy { getNewHelper(this) }

    open val isConfigurable = true
    override val context get() = applicationContext
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflateBinding(layoutInflater)
        if (!isConfigurable) {
            taskerHelper.finishForTasker()
            return
        }
        binding?.root?.let { setContentView(it) }
        taskerHelper.onCreate()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
            val result = taskerHelper.onBackPressed()
            if (result is SimpleResultError) {
                alert("Warning", "Settings are not valid:\n\n${result.message}")
            }
            return result.success
        } else super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
    }
}


abstract class ActivityConfigTaskerNoOutput<TInput : Any, TActionRunner : TaskerPluginRunner<TInput, Unit>, THelper : TaskerPluginConfigHelper<TInput, Unit, TActionRunner>, TBinding : ViewBinding> : ActivityConfigTasker<TInput, Unit, TActionRunner, THelper, TBinding>()
abstract class ActivityConfigTaskerNoInput<TOutput : Any, TActionRunner : TaskerPluginRunner<Unit, TOutput>, THelper : TaskerPluginConfigHelper<Unit, TOutput, TActionRunner>> : ActivityConfigTasker<Unit, TOutput, TActionRunner, THelper, ViewBinding>() {
    override fun assignFromInput(input: TaskerInput<Unit>) {}
    override val inputForTasker = TaskerInput(Unit)
    override fun inflateBinding(layoutInflater: LayoutInflater) = null
    override val isConfigurable = false
}

abstract class ActivityConfigTaskerNoOutputOrInput<TActionRunner : TaskerPluginRunner<Unit, Unit>, THelper : TaskerPluginConfigHelper<Unit, Unit, TActionRunner>> : ActivityConfigTaskerNoInput<Unit, TActionRunner, THelper>()