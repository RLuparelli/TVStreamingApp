package com.tvstreaming.app.core.utils

import com.tvstreaming.app.core.api.exceptions.ApiException
import com.tvstreaming.app.core.api.exceptions.NetworkException
import com.tvstreaming.app.core.api.exceptions.UnauthorizedException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    apiCall: suspend () -> Response<T>
): Resource<T> {
    return withContext(dispatcher) {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Resource.Success(body)
                } else {
                    Resource.Error(
                        ApiException("Empty response body", response.code()),
                        "Empty response from server"
                    )
                }
            } else {
                Resource.Error(
                    ApiException(response.message(), response.code()),
                    "Error: ${response.code()} ${response.message()}"
                )
            }
        } catch (e: UnauthorizedException) {
            Timber.e(e, "Unauthorized access")
            Resource.Error(e, "Session expired. Please login again.")
        } catch (e: NetworkException) {
            Timber.e(e, "Network error")
            Resource.Error(e, "Network error. Please check your connection.")
        } catch (e: IOException) {
            Timber.e(e, "IO Exception")
            Resource.Error(e, "Connection error. Please try again.")
        } catch (e: Exception) {
            Timber.e(e, "Unknown error")
            Resource.Error(e, e.message ?: "An unexpected error occurred")
        }
    }
}

// Simple version for non-Response returns
suspend fun <T> safeCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    call: suspend () -> T
): Resource<T> {
    return withContext(dispatcher) {
        try {
            Resource.Success(call())
        } catch (e: Exception) {
            Timber.e(e, "Error in safe call")
            Resource.Error(e, e.message ?: "An error occurred")
        }
    }
}