package com.joaomgcd.taskerpluginlibrary.output.runner

import java.lang.reflect.Method



sealed class TaskerValueGetter {
    abstract fun getValue(obj: Any?): Any?
    abstract val isArray: Boolean
}

class TaskerValueGetterMethod(val method: Method) : TaskerValueGetter() {
    override fun getValue(obj: Any?): Any? {
        if (obj == null) return null
        return try {
            val value = method.invoke(obj) ?: return null
            when (value) {
                is String -> value
                is Array<*> -> value
                is Boolean, Int, Long -> value.toString()
                is Float -> value.toString()
                is Double -> value.toString()
                is Collection<*> -> value.toTypedArray()
                else -> value.toString()
            }
        } catch (t: Throwable) {
            null
        }
    }

    override val isArray = method.returnType.isArray
}

class TaskerValueGetterDirect private constructor(val value: Any?) : TaskerValueGetter() {
    constructor(value: String?) : this(value as Any?)
    constructor(value: Array<*>?) : this(value as Any?)

    override fun getValue(obj: Any?): Any? = value
    override val isArray = if (value == null) false else value::class.java.isArray
}