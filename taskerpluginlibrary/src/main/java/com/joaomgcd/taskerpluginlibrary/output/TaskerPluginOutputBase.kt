package com.joaomgcd.taskerpluginlibrary.output

import android.content.Context
import android.os.Build
import com.joaomgcd.taskerpluginlibrary.extensions.addOrCreate
import com.joaomgcd.taskerpluginlibrary.extensions.taskerOutputCompatible
import java.lang.reflect.Method




abstract class TaskerOuputBase(var nameNoSuffix: String, val isMultiple: Boolean = false, val minApi: Int = -1, val maxApi: Int = Int.MAX_VALUE, var ignore: Boolean = false) {

    val nameTaskerCompatible get() = nameNoSuffix.taskerOutputCompatible
    val name: String
        get() {
            val result = nameTaskerCompatible
            return if (!isMultiple) result else "$result()"
        }

    constructor(context: Context, taskerVariable: TaskerOutputVariable, isMultiple: Boolean = false) : this(taskerVariable.name, isMultiple, taskerVariable.minApi, taskerVariable.maxApi)
    constructor(context: Context, taskerVariable: TaskerOutputVariable, method: Method) : this(context, taskerVariable, method.returnType.isArray)

}


abstract class TaskerOutputBase<TTaskerVariable : TaskerOuputBase> : ArrayList<TTaskerVariable>() {
    abstract fun getTaskerVariable(context: Context, taskerVariable: TaskerOutputVariable, method: Method, parent: Any?, isThisList: Boolean, isBaseList: Boolean, index: ArrayList<Int>? = null): List<TTaskerVariable>
    fun add(context: Context, type: Class<*>, parent: Any? = null, filter: ((TTaskerVariable) -> Boolean)? = null, isBaseList: Boolean = false, index: ArrayList<Int>? = null) {
        var realType = type
        val isList = realType.isArray
        if (isList) {
            realType = realType.componentType
        }
        val methods = realType.methods
        val (variables, other) = methods.partition { it.isAnnotationPresent(TaskerOutputVariable::class.java) }
        addAll(variables
                .map {
                    getTaskerVariable(context, it.getAnnotation(TaskerOutputVariable::class.java), it, parent, isList, isBaseList, index)
                }.flatten().map {
                    it.apply { ignore = filter?.let { filter -> !filter(this) } ?: false }
                }
                .filtered
        )
        other
                .filter { it.returnType.isAnnotationPresent(TaskerOutputObject::class.java) || it.returnType.componentType?.isAnnotationPresent(TaskerOutputObject::class.java) == true }
                .forEach { method ->
                    if (parent == null) {
                        add(context, method.returnType, parent, filter, isList)
                    } else {
                        if (!isList) {
                            add(context, method.returnType, method.invoke(parent), filter, isList)
                        } else {
                            (parent as Array<*>).forEachIndexed { indexForThis, parent ->
                                add(context, method.returnType, method.invoke(parent), filter, isList, index.addOrCreate(indexForThis + 1))
                            }

                        }
                    }
                }
    }


    //    inline fun <reified T> add(context: Context) = add(context, T::class.java)
    fun add(vararg taskerVariableInfo: TTaskerVariable) {
        addAll(taskerVariableInfo.filtered)
    }

    override fun add(element: TTaskerVariable): Boolean {
        if (!getTaskerFilter(element)) return false
        return super.add(element)
    }

    override fun add(index: Int, element: TTaskerVariable) {
        if (!getTaskerFilter(element)) return
        super.add(index, element)
    }

    override fun addAll(elements: Collection<TTaskerVariable>): Boolean {
        return super.addAll(elements.filtered)
    }

    override fun addAll(index: Int, elements: Collection<TTaskerVariable>): Boolean {
        return super.addAll(index, elements.filtered)
    }


    fun getByName(name: String) = firstOrNull { it.nameNoSuffix == name }

    fun renameIfNeeded(oldName: String, newName: CharSequence?) {
        if (newName == null || newName.isEmpty()) return
        getByName(oldName)?.nameNoSuffix = newName.toString()
    }

    private val Collection<TTaskerVariable>.filtered get() = filterForTasker(this)
    private val Array<out TTaskerVariable>.filtered get() = filterForTasker(this.toList())
    private fun filterForTasker(output: Collection<TTaskerVariable>): List<TTaskerVariable> = output.filter { getTaskerFilter(it) }
    private fun getTaskerFilter(it: TTaskerVariable) = !it.ignore && Build.VERSION.SDK_INT >= it.minApi && Build.VERSION.SDK_INT <= it.maxApi
}