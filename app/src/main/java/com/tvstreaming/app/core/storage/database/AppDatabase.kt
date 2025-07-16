package com.tvstreaming.app.core.storage.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.tvstreaming.app.core.storage.dao.ContentDao
import com.tvstreaming.app.core.storage.entities.*

@Database(
    entities = [
        ContentEntity::class,
        CategoryEntity::class,
        WatchProgressEntity::class,
        UserSessionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun contentDao(): ContentDao
    
    companion object {
        const val DATABASE_NAME = "tv_streaming_db"
        
        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .build()
        }
    }
}

