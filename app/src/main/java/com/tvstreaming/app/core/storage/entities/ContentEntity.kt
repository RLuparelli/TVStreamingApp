package com.tvstreaming.app.core.storage.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tvstreaming.app.models.ContentItem
import com.tvstreaming.app.models.UserInfo
import com.tvstreaming.app.models.ServerInfo

@Entity(tableName = "content")
data class ContentEntity(
    @PrimaryKey val id: String,
    val name: String,
    val streamType: String,
    val streamIcon: String?,
    val rating: String?,
    val categoryId: String,
    val containerExtension: String?,
    val added: String?,
    val customSid: String?,
    val directSource: String?,
    val cachedAt: Long = System.currentTimeMillis(),
    val lastAccessed: Long = System.currentTimeMillis()
) {
    fun toContentItem(): ContentItem {
        return ContentItem(
            streamId = id,
            name = name,
            streamType = streamType,
            streamIcon = streamIcon,
            rating = rating,
            categoryId = categoryId,
            containerExtension = containerExtension,
            added = added,
            customSid = customSid,
            directSource = directSource
        )
    }
    
    companion object {
        fun fromContentItem(item: ContentItem): ContentEntity {
            return ContentEntity(
                id = item.streamId,
                name = item.name,
                streamType = item.streamType,
                streamIcon = item.streamIcon,
                rating = item.rating,
                categoryId = item.categoryId,
                containerExtension = item.containerExtension,
                added = item.added,
                customSid = item.customSid,
                directSource = item.directSource
            )
        }
    }
}


@Entity(tableName = "watch_progress")
data class WatchProgressEntity(
    @PrimaryKey val contentId: String,
    val position: Long,
    val duration: Long,
    val lastWatched: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false
)

@Entity(tableName = "user_session")
data class UserSessionEntity(
    @PrimaryKey val id: String = "current_session",
    val username: String,
    val password: String,
    val token: String?,
    val refreshToken: String?,
    val expiresAt: Long,
    val tenantId: String,
    val createdAt: Long = System.currentTimeMillis()
)