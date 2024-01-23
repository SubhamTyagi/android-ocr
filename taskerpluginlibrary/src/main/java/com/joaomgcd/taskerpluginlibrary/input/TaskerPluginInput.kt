package com.joaomgcd.taskerpluginlibrary.input

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.joaomgcd.taskerpluginlibrary.NoEmptyConstructorException
import com.joaomgcd.taskerpluginlibrary.STRING_RES_ID_NOT_SET
import com.joaomgcd.taskerpluginlibrary.extensions.putTaskerCompatibleInput
import com.joaomgcd.taskerpluginlibrary.extensions.taskerPluginExtraBundle
import com.joaomgcd.taskerpluginlibrary.extensions.wasConfiguredBefore
import com.joaomgcd.taskerpluginlibrary.getForTaskerCompatibleInputTypes
import com.joaomgcd.taskerpluginlibrary.getStringFromResourceIdOrResourceName
import java.lang.reflect.Field


open class TaskerInputInfo(val key: String, val label: String?, val description: String?, val ignoreInStringBlurb: Boolean, open var value: Any?, val order: Int = Int.MAX_VALUE) {
    fun <T> valueAs() = value as T
}

open class TaskerInputInfoDynamic(key: String, label: String?, description: String?, ignoreInStringBlurb: Boolean, private val getter: () -> Any?, private val setter: ((Any?) -> Unit)? = null, order: Int = Int.MAX_VALUE) : TaskerInputInfo(key, label, description, ignoreInStringBlurb, null, order) {
    val Any?.isEmpty
        get() = getForTaskerCompatibleInputTypes(this,
                { true },
                { it.isEmpty() },
                { false },
                { false },
                { false },
                { false },
                { false },
                { false },
                { false })
    override var value: Any?
        get() {
            return try {
                getter()
            } catch (t: Throwable) {
                t.printStackTrace()
                null
            }
        }
        set(value) {
            if (value == null || value.isEmpty) return
            try {
                setter?.invoke(value)
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }


}

class TaskerInputInfoField(key: String, label: String, description: String?, ignoreInStringBlurb: Boolean, val taskerPluginInput: Any, private val getter: Field, order: Int = Int.MAX_VALUE) :
        TaskerInputInfoDynamic(key, label, description, ignoreInStringBlurb, { getter.apply { isAccessible = true }.get(taskerPluginInput) }, { getter.apply { isAccessible = true }.set(taskerPluginInput, it) }, order) {
//    companion object {
//        private fun getSetter(taskerPluginInput: Any, getter: Method): ((Any?) -> Unit)? {
//            return {
//
//                val setter: Method? = try {
//                    taskerPluginInput::class.java.getDeclaredMethod(getter.name.replace("get", "set"), getter.returnType)
//                } catch (t: Throwable) {
//                    t.printStackTrace()
//                    null
//                }
//                setter?.invoke(taskerPluginInput, it)
//            }
//        }
//    }

}

class TaskerInputInfos : ArrayList<TaskerInputInfo>() {
    companion object {
        fun fromInput(context: Context, input: TaskerInput<*>) = fromInput(context, input.regular).apply { addAll(input.dynamic) }
        fun fromInput(context: Context, input: Any) = TaskerInputInfos().apply { addFromInput(context, input) }
        fun fromBundle(context: Context, input: Any, bundle: Bundle) = fromInput(context, input).apply {
            forEach {
                it.value = bundle.get(it.key)
            }
            bundle.keySet().forEach { key ->
                bundle.get(key)?.let { value -> add(TaskerInputInfo(key, null, null, true, value)) }

            }
        }
    }

    private fun getString(context: Context, resId: Int, resIdName: String, defaultString:String) = context.getStringFromResourceIdOrResourceName(resId,resIdName,defaultString)

    fun addFromInput(context: Context, taskerPluginInput: Any, parentKey: String? = null) {
        val inputClass = taskerPluginInput::class.java
        if (inputClass == Unit::class.java) return
        if (!inputClass.isAnnotationPresent(TaskerInputRoot::class.java) && !inputClass.isAnnotationPresent(TaskerInputObject::class.java)) {
            throw RuntimeException("Input types must be annotated by either TaskerInputRoot or TaskerInputObject. $inputClass has none.")
        }
        val fields = inputClass.declaredFields
        val (inputFields, other) = fields.partition { it.isAnnotationPresent(TaskerInputField::class.java) }
        addAll(inputFields
                .map { method ->
                    val annotation = method.getAnnotation(TaskerInputField::class.java)
                    var key = annotation.key
                    if (parentKey != null) key = "$parentKey.$key"
                    val label = getString(context, annotation.labelResId,annotation.labelResIdName,key)
                    val description = getString(context, annotation.descriptionResId, annotation.descriptionResIdName,"")
                    TaskerInputInfoField(key, label, description, annotation.ignoreInStringBlurb, taskerPluginInput, method)
                }
        )
        other
                .filter { it.type.isAnnotationPresent(TaskerInputObject::class.java) }
                .forEach { method ->
                    val annotation = method.type.getAnnotation(TaskerInputObject::class.java)
                    val inputObject: Any? = method.apply { isAccessible = true }.get(taskerPluginInput)
                    inputObject?.let {
                        var key = annotation.key
                        method.getAnnotation(TaskerInputObject::class.java)?.key?.let { key = "$key.$it" }
                        addFromInput(context, it, key)
                    }
                }


    }

    fun getByKey(key: String) = firstOrNull { it.key == key }
    val bundle get() = Bundle().apply { toExistingBundle(this) }
    fun toExistingBundle(bundle: Bundle) = filter { bundle.putTaskerCompatibleInput(it.key, it.value) }
}

class TaskerInput<TInput : Any>(val regular: TInput, val dynamic: TaskerInputInfos = TaskerInputInfos())

internal fun <TInput : Any> getInputFromTaskerIntent(context: Context, taskerIntent: Intent?, inputClass: Class<TInput>, defaultInput: TInput? = null): TaskerInput<TInput> {
    //    if (inputClass == Unit::class.java) return TaskerInput(Unit) as TaskerInput<TInput>
    fun getNewInstance() = try {
        if (inputClass == Unit::class.java) {
            Unit
        } else {
            inputClass.newInstance()
        } as TInput

    } catch (t: Throwable) {
        throw NoEmptyConstructorException(inputClass.name)
    }

    fun getInput(): TInput {
        val wasConfiguredBefore = taskerIntent?.taskerPluginExtraBundle?.wasConfiguredBefore
                ?: false
       return if (!wasConfiguredBefore) {
            defaultInput ?: getNewInstance()
        } else {
            getNewInstance()
        }
    }



    if (taskerIntent == null) return TaskerInput(getInput())
    val extrasBundle = taskerIntent.taskerPluginExtraBundle
    val input = getInput()
    val inputInfos = TaskerInputInfos.fromBundle(context, input, extrasBundle)
    return TaskerInput(input, inputInfos)
}

