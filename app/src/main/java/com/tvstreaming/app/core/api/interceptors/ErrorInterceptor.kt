package com.tvstreaming.app.core.api.interceptors

import com.google.gson.Gson
import com.tvstreaming.app.core.api.exceptions.ApiException
import com.tvstreaming.app.core.api.exceptions.NetworkException
import com.tvstreaming.app.core.api.exceptions.UnauthorizedException
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorInterceptor @Inject constructor(
    private val gson: Gson
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        try {
            val response = chain.proceed(request)
            
            when (response.code) {
                401 -> {
                    throw UnauthorizedException("Token expired or invalid")
                }
                403 -> {
                    throw ApiException("Access forbidden", response.code)
                }
                404 -> {
                    throw ApiException("Resource not found", response.code)
                }
                in 500..599 -> {
                    throw ApiException("Server error", response.code)
                }
            }
            
            return response
        } catch (e: IOException) {
            Timber.e(e, "Network error")
            throw NetworkException("Network error: ${e.message}", e)
        }
    }
}