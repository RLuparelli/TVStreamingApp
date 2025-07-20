package com.tvstreaming.app.core.api.interceptors

import com.tvstreaming.app.core.network.NetworkMonitor
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CacheInterceptor @Inject constructor(
    private val networkMonitor: NetworkMonitor
) : Interceptor {
    
    companion object {
        private const val CACHE_CONTROL_HEADER = "Cache-Control"
        private const val CACHE_MAX_AGE = 5 // 5 seconds for online
        private const val CACHE_MAX_STALE = 7 // 7 days for offline
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        
        request = if (networkMonitor.isConnected()) {
            // If online, cache for a short time
            request.newBuilder()
                .header(CACHE_CONTROL_HEADER, "public, max-age=$CACHE_MAX_AGE")
                .build()
        } else {
            // If offline, use cache up to 7 days old
            request.newBuilder()
                .header(
                    CACHE_CONTROL_HEADER,
                    "public, only-if-cached, max-stale=${TimeUnit.DAYS.toSeconds(CACHE_MAX_STALE.toLong())}"
                )
                .build()
        }
        
        return chain.proceed(request)
    }
}