package com.tvstreaming.app.core.api.interceptors

import com.tvstreaming.app.core.storage.preferences.SecurePreferences
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val securePreferences: SecurePreferences
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip auth for login endpoints
        if (originalRequest.url.encodedPath.contains("auth/")) {
            return chain.proceed(originalRequest)
        }
        
        val token = securePreferences.getAuthToken()
        
        val request = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .header("X-Platform", "android")
                .header("X-Device-Type", if (securePreferences.isTV()) "tv" else "mobile")
                .build()
        } else {
            originalRequest
        }
        
        return chain.proceed(request)
    }
}