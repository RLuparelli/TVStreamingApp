package com.tvstreaming.app.core.storage.dao

import androidx.room.*
import com.tvstreaming.app.core.storage.entities.ContentEntity
import com.tvstreaming.app.core.storage.entities.CategoryEntity
import com.tvstreaming.app.core.storage.entities.WatchProgressEntity
import com.tvstreaming.app.core.storage.entities.UserSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentDao {
    
    // Content operations
    @Query("SELECT * FROM content WHERE categoryId = :categoryId ORDER BY name ASC")
    suspend fun getContentByCategory(categoryId: String): List<ContentEntity>
    
    @Query("SELECT * FROM content WHERE categoryId = :categoryId ORDER BY name ASC")
    fun getContentByCategoryFlow(categoryId: String): Flow<List<ContentEntity>>
    
    @Query("SELECT * FROM content WHERE id = :contentId")
    suspend fun getContentById(contentId: String): ContentEntity?
    
    @Query("SELECT * FROM content WHERE name LIKE :query ORDER BY name ASC")
    suspend fun searchContent(query: String): List<ContentEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContent(content: List<ContentEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContent(content: ContentEntity)
    
    @Query("DELETE FROM content WHERE categoryId = :categoryId")
    suspend fun deleteContentByCategory(categoryId: String)
    
    @Query("DELETE FROM content WHERE cachedAt < :timestamp")
    suspend fun deleteOldContent(timestamp: Long)
    
    @Query("UPDATE content SET lastAccessed = :timestamp WHERE id = :contentId")
    suspend fun updateLastAccessed(contentId: String, timestamp: Long)
    
    // Category operations
    @Query("SELECT * FROM categories ORDER BY name ASC")
    suspend fun getAllCategories(): List<CategoryEntity>
    
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategoriesFlow(): Flow<List<CategoryEntity>>
    
    @Query("SELECT * FROM categories WHERE parentId = :parentId ORDER BY name ASC")
    suspend fun getCategoriesByParent(parentId: Int): List<CategoryEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)
    
    @Query("DELETE FROM categories WHERE cachedAt < :timestamp")
    suspend fun deleteOldCategories(timestamp: Long)
    
    // Watch progress operations
    @Query("SELECT * FROM watch_progress WHERE contentId = :contentId")
    suspend fun getWatchProgress(contentId: String): WatchProgressEntity?
    
    @Query("SELECT * FROM watch_progress ORDER BY lastWatched DESC LIMIT :limit")
    suspend fun getRecentWatchProgress(limit: Int = 20): List<WatchProgressEntity>
    
    @Query("SELECT * FROM watch_progress WHERE isCompleted = 0 ORDER BY lastWatched DESC")
    suspend fun getContinueWatching(): List<WatchProgressEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchProgress(progress: WatchProgressEntity)
    
    @Query("DELETE FROM watch_progress WHERE contentId = :contentId")
    suspend fun deleteWatchProgress(contentId: String)
    
    @Query("DELETE FROM watch_progress WHERE lastWatched < :timestamp")
    suspend fun deleteOldWatchProgress(timestamp: Long)
    
    // User session operations
    @Query("SELECT * FROM user_session WHERE id = 'current_session'")
    suspend fun getCurrentSession(): UserSessionEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: UserSessionEntity)
    
    @Query("DELETE FROM user_session")
    suspend fun clearSession()
    
    // Utility operations
    @Query("SELECT COUNT(*) FROM content")
    suspend fun getContentCount(): Int
    
    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoryCount(): Int
    
    @Transaction
    suspend fun clearAllData() {
        clearSession()
        deleteOldContent(System.currentTimeMillis())
        deleteOldCategories(System.currentTimeMillis())
        deleteOldWatchProgress(0)
    }
}