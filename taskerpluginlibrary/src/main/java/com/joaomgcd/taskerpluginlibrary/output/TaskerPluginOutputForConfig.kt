package com.joaomgcd.taskerpluginlibrary.output

import android.content.Context
import com.joaomgcd.taskerpluginlibrary.extensions.splitWordsTitleCase
import com.joaomgcd.taskerpluginlibrary.getStringFromResourceIdOrResourceName
import java.lang.reflect.Method


class TaskerOutputForConfig(nameNoSuffix: String, val label: String, val htmlLabel: String, isMultiple: Boolean = false, minApi: Int = -1, maxApi: Int = Int.MAX_VALUE) : TaskerOuputBase(nameNoSuffix, isMultiple, minApi, maxApi) {
    constructor(context: Context, taskerVariable: TaskerOutputVariable, isMultiple: Boolean = false) : this(
        taskerVariable.name,
        context.getStringFromResourceIdOrResourceName(taskerVariable.labelResId, taskerVariable.labelResIdName, taskerVariable.name.splitWordsTitleCase),
        context.getStringFromResourceIdOrResourceName(taskerVariable.htmlLabelResId, taskerVariable.htmlLabelResIdName, ""),
        isMultiple,
        taskerVariable.minApi,
        taskerVariable.maxApi
    )

    constructor(context: Context, taskerVariable: TaskerOutputVariable, method: Method, isThisList: Boolean, isBaseList: Boolean) : this(context, taskerVariable, method.returnType.isArray || isThisList || isBaseList)

    override fun toString() = "%$name\n$label\n$htmlLabel"
}

class TaskerOutputsForConfig : TaskerOutputBase<TaskerOutputForConfig>() {
    override fun getTaskerVariable(context: Context, taskerVariable: TaskerOutputVariable, method: Method, parent: Any?, isThisList: Boolean, isBaseList: Boolean, index: ArrayList<Int>?) = TaskerOutputsForConfig().apply { add(TaskerOutputForConfig(context, taskerVariable, method, isThisList, isBaseList)) }

}
