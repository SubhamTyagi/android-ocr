package com.joaomgcd.taskerpluginlibrary.output.runner

import android.content.Context
import android.os.Bundle
import com.joaomgcd.taskerpluginlibrary.extensions.addOrCreate
import com.joaomgcd.taskerpluginlibrary.output.TaskerOuputBase
import com.joaomgcd.taskerpluginlibrary.output.TaskerOutputBase
import com.joaomgcd.taskerpluginlibrary.output.TaskerOutputVariable
import com.joaomgcd.taskerpluginlibrary.runner.TaskerOutputRenames
import java.lang.reflect.Method




open class TaskerOutputForRunner(nameNoSuffix: String, val valueGetter: TaskerValueGetter, val parent: Any? = null, minApi: Int = -1, maxApi: Int = Int.MAX_VALUE, val index: ArrayList<Int>? = null) : TaskerOuputBase(getName(nameNoSuffix, index), valueGetter.isArray, minApi, maxApi) {
    constructor(nameNoSuffix: String, value: String?) : this(nameNoSuffix, TaskerValueGetterDirect(value))
    constructor(nameNoSuffix: String, value: Array<*>?) : this(nameNoSuffix, TaskerValueGetterDirect(value))
    constructor(nameNoSuffix: String, value: Collection<*>?) : this(nameNoSuffix, TaskerValueGetterDirect(value?.toTypedArray()))

    constructor(context: Context, taskerVariable: TaskerOutputVariable, valueGetter: TaskerValueGetter, parent: Any?, index: ArrayList<Int>? = null) : this(taskerVariable.name, valueGetter, parent, taskerVariable.minApi, taskerVariable.maxApi, index)

    val value get() = valueGetter.getValue(parent)
    fun addToBundle(bundle: Bundle) {
        val value = this.value ?: return
        val values = getValues(value)
        if (values.isEmpty()) return

        val name = nameTaskerCompatible
        for (i in 0 until values.size) {
            val indexedName = if (isMultiple) {
                "$name${i + 1}"
            } else {
                name
            }
            bundle.putString("%$indexedName", values[i])
        }
    }

    private fun getValues(value: Any): Array<String> {
        val result: Array<*> = if (isMultiple) {
            value as Array<*>
        } else {
            arrayOf(value)
        }
        return result.map { it.toString() }.toTypedArray()
    }

    private companion object {
        fun getName(name: String, index: ArrayList<Int>?): String {
            if (index == null || index.size == 0) return name
            return "$name${index[0]}"
        }
    }
}


class TaskerOutputsForRunner : TaskerOutputBase<TaskerOutputForRunner>() {
    override fun getTaskerVariable(context: Context, taskerVariable: TaskerOutputVariable, method: Method, parent: Any?, isThisList: Boolean, isBaseList: Boolean, index: ArrayList<Int>?): TaskerOutputsForRunner {
        val result = TaskerOutputsForRunner()
        if (!isThisList) {
            return result.apply { add(TaskerOutputForRunner(context, taskerVariable, TaskerValueGetterMethod(method), parent, index)) }
        } else {
            val parentAsArray = parent as Array<*>
            for (i in 0 until parentAsArray.size) {
                val parentInArray = parentAsArray[i]
                result.add(TaskerOutputForRunner(context, taskerVariable, TaskerValueGetterMethod(method), parentInArray, index.addOrCreate(i + 1)))
            }
            return result
        }
    }


    companion object {
        private data class NameAndIndex(val name: String, val index: Int?)



        @JvmStatic
        fun getVariableBundle(context: Context, regularOutput: Any? = null, dynamicOutput: TaskerOutputsForRunner? = null, renames: TaskerOutputRenames? = null, filter: ((TaskerOutputForRunner) -> Boolean)? = null): Bundle {
            return Bundle().apply {
                TaskerOutputsForRunner().apply {
                    dynamicOutput?.let { addAll(it) }
                }.apply {
                    regularOutput?.let { add(context, it::class.java, it, filter) }
                }.apply {
                    renames?.rename(this)
                }.groupBy {
                    val index = if (it.index == null || it.index.size == 0) null else it.index[0]
                    NameAndIndex(it.name, index)
                }.map { group ->
                    val first = group.value[0]
                    if (group.value.size == 1) {
                        first
                    } else {
                        val value = group.value.joinToString(",") { output -> first.valueGetter.getValue(output.parent).toString() }
                        TaskerOutputForRunner(first.name, value)

                    }
                }.forEach {
                    it.addToBundle(this)
                }
            }
        }
    }
}