package com.joaomgcd.taskerpluginlibrary

import android.content.Context


/**
 * Does a different action for a value, depending on its type. Only Tasker supported types are considered
 *
 * @param value the value to act on
 */
fun <TResult> getForTaskerCompatibleInputTypes(value: Any?,
                                               forNull: (Any?) -> TResult,
                                               forString: (String) -> TResult,
                                               forInt: (Int) -> TResult,
                                               forLong: (Long) -> TResult,
                                               forFloat: (Float) -> TResult,
                                               forDouble: (Double) -> TResult,
                                               forBoolean: (Boolean) -> TResult,
                                               forStringArray: (Array<String>) -> TResult,
                                               forStringArrayList: (ArrayList<String>) -> TResult): TResult {
    if (value == null) return forNull(value)
    return when (value) {
        is String -> forString(value)
        is Int -> forInt(value)
        is Long -> forLong(value)
        is Float -> forFloat(value)
        is Double -> forDouble(value)
        is Boolean -> forBoolean(value)
        is Array<*> -> forStringArray(value as Array<String>)
        is ArrayList<*> -> forStringArrayList(value as ArrayList<String>)
        else -> throw RuntimeException("Tasker doesn't support inputs of type ${value::class.java}")
    }
}

fun Context.getStringResourceId(resourceName: String): Int {
    return resources.getIdentifier(resourceName, "string", packageName)
}

const val STRING_RES_ID_NOT_SET = -1
const val STRING_RES_ID_NAME_NOT_SET = ""

fun Context.getStringFromResourceIdOrResourceName(resourceId: Int, resourceName: String, defaultString: String): String {
    val resourceIdSet = resourceId != STRING_RES_ID_NOT_SET
    val resourceNameIdSet = resourceName != STRING_RES_ID_NAME_NOT_SET
    if (!resourceIdSet && !resourceNameIdSet) return defaultString

    val resourceIdFinal = if (resourceNameIdSet) getStringResourceId(resourceName) else resourceId
    return try {
        getString(resourceIdFinal)
    } catch (t: Throwable) {
        defaultString
    }

}