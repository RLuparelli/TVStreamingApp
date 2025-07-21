package com.tvstreaming.app.core.di

import com.tvstreaming.app.core.api.ApiService
import com.tvstreaming.app.core.repositories.*
import com.tvstreaming.app.core.storage.dao.ContentDao
import com.tvstreaming.app.core.storage.preferences.SecurePreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideMediaRepository(
        apiService: ApiService
    ): MediaRepository {
        return MediaRepository(apiService)
    }
    
    @Provides
    @Singleton
    fun provideChannelRepository(
        apiService: ApiService
    ): ChannelRepository {
        return ChannelRepository(apiService)
    }
    
    @Provides
    @Singleton
    fun provideContentRepository(
        apiService: ApiService,
        contentDao: ContentDao
    ): ContentRepository {
        return ContentRepository(apiService, contentDao)
    }
    
    @Provides
    @Singleton
    fun provideContentDetailRepository(
        apiService: ApiService
    ): ContentDetailRepository {
        return ContentDetailRepository(apiService)
    }
}