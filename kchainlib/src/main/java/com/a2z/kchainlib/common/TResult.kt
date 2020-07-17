package com.a2z.kchainlib.common

sealed class TResult<out T: Any> {
    data class Success<out T: Any>(val value: T) : TResult<T>()
    data class Error(
        val code: Int = -1,
        val message: String = "unknown error",
        val cause: Exception? = null
    ) : TResult<Nothing>()
}

val <T> T.exhaustive: T
    get() = this