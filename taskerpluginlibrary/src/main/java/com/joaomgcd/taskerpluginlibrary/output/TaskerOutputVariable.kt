package com.joaomgcd.taskerpluginlibrary.output

import com.joaomgcd.taskerpluginlibrary.STRING_RES_ID_NAME_NOT_SET
import com.joaomgcd.taskerpluginlibrary.STRING_RES_ID_NOT_SET

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class TaskerOutputVariable(val name: String, val labelResId: Int = STRING_RES_ID_NOT_SET, val htmlLabelResId: Int = STRING_RES_ID_NOT_SET, val minApi: Int = -1, val maxApi: Int = Int.MAX_VALUE, val labelResIdName:String=STRING_RES_ID_NAME_NOT_SET, val htmlLabelResIdName:String=STRING_RES_ID_NAME_NOT_SET)


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class TaskerOutputObject()