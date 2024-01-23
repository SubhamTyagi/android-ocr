package com.joaomgcd.taskerpluginlibrary.extensions

import android.app.Activity
import android.content.Context
import com.joaomgcd.taskerpluginlibrary.condition.TaskerPluginRunnerCondition

/**
 * Will convert a string to a Tasker compatible variable name
 */
val String.taskerOutputCompatible: String
    get() {
        var s = this.trim().replace(" ", "").replace("%", "").replace(" ", "_").replace("[]", "").replace("-", "").replace("'", "_").replace("\\[[0-9]+\\]".toRegex(), "").toLowerCase()
        if (s.length < 3) {
            s = pad(s, "a", 3, false)
        }
        return s
    }

/**
 * Adds a label and string followed by a new line in the following format - Label: value
 * Can be handy to use when building the string blub output for the Tasker condition/action
 */
fun StringBuilder.addTaskerInput(label: String?, value: Any?) {
    if (value == null || label == null) return

    val finalValue = when (value) {
        is Boolean -> if (value) "true" else null
        else -> value.toString()
    }
    if (finalValue.isNullOrEmpty()) return

    if (length > 0) {
        append("\n")
    }
    append("$label: $finalValue")
}

/**
 * Shorthand method to request Tasker to query a certain condition
 */
fun <T : Activity> Class<T>.requestQuery(context: Context, update: Any? = null) = TaskerPluginRunnerCondition.requestQuery(context, this, update)


private fun pad(s: String, ch: String, n: Int, right: Boolean): String {
    val realN = n - s.length
    val pad = String.format("%0" + realN + "d", 0).replace("0", ch)
    val leftPad = if (right) "" else pad
    val rightPad = if (right) pad else ""
    return leftPad + s + rightPad
}