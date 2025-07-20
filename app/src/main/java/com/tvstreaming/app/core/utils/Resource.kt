package com.tvstreaming.app.core.utils

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    val error: Throwable? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(error: Throwable, message: String = error.message ?: "Unknown error", data: T? = null) : 
        Resource<T>(data, message, error)
}

// Extension functions for easier handling
fun <T> Resource<T>.onSuccess(action: (value: T) -> Unit): Resource<T> {
    if (this is Resource.Success) {
        data?.let(action)
    }
    return this
}

fun <T> Resource<T>.onError(action: (error: Throwable) -> Unit): Resource<T> {
    if (this is Resource.Error) {
        error?.let(action)
    }
    return this
}

fun <T> Resource<T>.onLoading(action: () -> Unit): Resource<T> {
    if (this is Resource.Loading) {
        action()
    }
    return this
}