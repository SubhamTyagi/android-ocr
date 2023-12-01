package com.joaomgcd.taskerpluginlibrary.input

import com.joaomgcd.taskerpluginlibrary.STRING_RES_ID_NAME_NOT_SET
import com.joaomgcd.taskerpluginlibrary.STRING_RES_ID_NOT_SET

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class TaskerInputRoot()

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class TaskerInputField(val key: String, val labelResId: Int = STRING_RES_ID_NOT_SET, val descriptionResId: Int = STRING_RES_ID_NOT_SET, val ignoreInStringBlurb: Boolean = false, val order: Int = Int.MAX_VALUE, val labelResIdName:String= STRING_RES_ID_NAME_NOT_SET, val descriptionResIdName:String= STRING_RES_ID_NAME_NOT_SET)

@Target(AnnotationTarget.CLASS,AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class TaskerInputObject(val key: String, val labelResId: Int = STRING_RES_ID_NOT_SET, val descriptionResId: Int = STRING_RES_ID_NOT_SET, val ignoreInStringBlurb: Boolean = false, val order: Int = Int.MAX_VALUE, val labelResIdName:String= STRING_RES_ID_NAME_NOT_SET, val descriptionResIdName:String= STRING_RES_ID_NAME_NOT_SET)