package com.tvstreaming.app.core.di

import android.content.Context
import androidx.room.Room
import com.tvstreaming.app.core.storage.database.AppDatabase
import com.tvstreaming.app.core.storage.dao.ContentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.create(context)
    }
    
    @Provides
    fun provideContentDao(database: AppDatabase): ContentDao {
        return database.contentDao()
    }
}