package com.joaomgcd.taskerpluginlibrary.config

import android.app.Activity
import android.content.Intent
import com.joaomgcd.taskerpluginlibrary.*
import com.joaomgcd.taskerpluginlibrary.extensions.*
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInputInfos
import com.joaomgcd.taskerpluginlibrary.output.TaskerOutputForConfig
import com.joaomgcd.taskerpluginlibrary.output.TaskerOutputsForConfig
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginRunner
import net.dinglisch.android.tasker.TaskerPlugin

abstract class TaskerPluginConfigHelper<TInput : Any, TOutput : Any, TActionRunner : TaskerPluginRunner<TInput, TOutput>>(val config: TaskerPluginConfig<TInput>) {

    abstract val inputClass: Class<TInput>
    abstract val runnerClass: Class<TActionRunner>
    abstract val outputClass: Class<TOutput>
    open val timeoutSeconds: Int = 60
    open val defaultInput: TInput? = null
    open fun isInputValid(input: TaskerInput<TInput>): SimpleResult = SimpleResultSuccess()

    protected val context by lazy { config.context }
    private val runner by lazy { runnerClass.newInstance() }
    private val taskerIntent = config.getIntent()
    val hostCapabilities = HostCapabilities(taskerIntent?.extras)
    val relevantVariables: Array<String> = TaskerPlugin.getRelevantVariableList(taskerIntent?.extras)
    val breadCrumbs = taskerIntent?.getStringExtra(TaskerPluginConstants.EXTRA_STRING_BREADCRUMB)

    private fun getInputInfos(input: TaskerInput<TInput>) = TaskerInputInfos.fromInput(config.context, input)
    private fun getTaskerIntentFromInput(stringBlurb: String?, output: TaskerOutputsForConfig, input: TaskerInput<TInput>): Intent {
        return Intent().apply {
            val extraBundle = this.taskerPluginExtraBundle

            extraBundle.wasConfiguredBefore = true
            extraBundle.runnerClass = runnerClass.name
            extraBundle.inputClass = inputClass.name
            getInputInfos(input).let {
                val added = it.toExistingBundle(extraBundle)
                val forReplacements = added.filter { it.value is String }
                extraBundle.putString(TaskerPluginConstants.VARIABLE_REPLACE_KEYS, forReplacements.joinToString(" ") { it.key })
            }

            stringBlurb?.let { putExtra(TaskerPluginConstants.EXTRA_STRING_BLURB, it) }
            output.add(TaskerOutputForConfig("err",context.getString(R.string.error_code) ,context.getString(R.string.error_code_description)))
            output.add(TaskerOutputForConfig("errmsg",context.getString(R.string.error_message) ,context.getString(R.string.error_message_description)))
            TaskerPlugin.addRelevantVariableList(this, output.map { it.toString() }.toTypedArray())
            TaskerPlugin.Setting.requestTimeoutMS(this, timeoutSeconds * 1000)
        }
    }

    fun finishForTasker(): SimpleResult {
        val input = config.inputForTasker.apply { addInputs(this.dynamic) }
        val isInputValid = isInputValid(input)
        if (!isInputValid.success) return isInputValid

        val output = TaskerOutputsForConfig().apply { addOutputs(input, this) }
        runner.getRenames(config.context, input)?.rename(output)
        val stringBlurb = getStringBlurb(input)

        config.setResult(Activity.RESULT_OK, getTaskerIntentFromInput(stringBlurb, output, input))
        config.finish()
        return isInputValid
    }

    fun onBackPressed(): SimpleResult {
        return finishForTasker()
    }

    fun onCreate() {
        config.assignFromInput(taskerIntent.getTaskerInput(config.context, inputClass, defaultInput))
    }

    private fun getStringBlurb(input: TaskerInput<TInput>) = StringBuilder().apply {
        if (addDefaultStringBlurb) {
            getInputInfos(input).forEach {
                if (it.ignoreInStringBlurb) return@forEach
                val value = inputTranslationsForStringBlurb[it.key]?.invoke(it.value) ?: it.value
                addTaskerInput(it.label, value)
            }
        }
        addToStringBlurb(input, this)
    }.toString()

    open val inputTranslationsForStringBlurb: HashMap<String, (Any?) -> String?> = HashMap()
    open val addDefaultStringBlurb = true
    open fun addToStringBlurb(input: TaskerInput<TInput>, blurbBuilder: StringBuilder) {}

    open fun addInputs(input: TaskerInputInfos) {

    }

    open fun addOutputs(input: TaskerInput<TInput>, output: TaskerOutputsForConfig) {
        outputClass?.let { output.add(config.context, it, filter = { runner.shouldAddOutput(config.context, input, it) }) }
    }

}
