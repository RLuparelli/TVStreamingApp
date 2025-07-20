package com.tvstreaming.app.core.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tvstreaming.app.BuildConfig
import com.tvstreaming.app.core.api.ApiService
import com.tvstreaming.app.core.api.interceptors.AuthInterceptor
import com.tvstreaming.app.core.api.interceptors.CacheInterceptor
import com.tvstreaming.app.core.api.interceptors.ErrorInterceptor
import com.tvstreaming.app.core.network.NetworkMonitor
import com.tvstreaming.app.core.storage.preferences.SecurePreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .create()
    
    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache {
        val cacheSize = 10 * 1024 * 1024L // 10 MB
        return Cache(context.cacheDir, cacheSize)
    }
    
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        // Create logger that uses Timber
        val logger = HttpLoggingInterceptor.Logger { message ->
            timber.log.Timber.tag("OkHttp").d(message)
        }
        
        return HttpLoggingInterceptor(logger).apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(
        cache: Cache,
        authInterceptor: AuthInterceptor,
        errorInterceptor: ErrorInterceptor,
        cacheInterceptor: CacheInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(authInterceptor)
            // .addInterceptor(errorInterceptor) // Temporarily disabled to avoid crashes
            .addInterceptor(cacheInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideAuthInterceptor(securePreferences: SecurePreferences): AuthInterceptor {
        return AuthInterceptor(securePreferences)
    }
    
    @Provides
    @Singleton
    fun provideCacheInterceptor(networkMonitor: NetworkMonitor): CacheInterceptor {
        return CacheInterceptor(networkMonitor)
    }
    
    @Provides
    @Singleton
    fun provideErrorInterceptor(gson: Gson): ErrorInterceptor {
        return ErrorInterceptor(gson)
    }
    
    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor {
        return NetworkMonitor(context)
    }
}