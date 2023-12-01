package com.joaomgcd.taskerpluginlibrary

/**
 * Convenience class to indicate if a process ended with success or failure.
 * If a success, you'll have access to an optional payload by using #SimpleResultSuccessWithPayload
 * If an error, you'll have access to an error message by using #SimpleResultError
 *
 */
sealed class SimpleResult(val success: Boolean) {
    companion object {
        fun <R> get(block: () -> R) = try {
            SimpleResultSuccessWithPayload(block())
        } catch (t: Throwable) {
            SimpleResultError(t)
        }

        fun <R> run(block: () -> Unit) = try {
            block()
            SimpleResultSuccess()
        } catch (t: Throwable) {
            SimpleResultError(t)
        }
    }
}

open class SimpleResultSuccess : SimpleResult(true)
class SimpleResultSuccessWithPayload<TPayload>(val payload: TPayload) : SimpleResultSuccess()

class SimpleResultError(val message: String) : SimpleResult(false) {
    constructor(t: Throwable) : this(t.message ?: t.toString())
}