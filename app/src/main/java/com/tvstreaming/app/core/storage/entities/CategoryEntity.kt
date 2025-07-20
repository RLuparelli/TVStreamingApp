package com.tvstreaming.app.core.storage.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tvstreaming.app.models.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val iconUrl: String? = null,
    val description: String? = null,
    val parentId: Int = 0,
    val order: Int = 0,
    val cachedAt: Long = System.currentTimeMillis()
) {
    fun toCategory(): Category {
        return Category(
            categoryId = id,
            categoryName = name,
            parentId = parentId
        )
    }
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = categoryId,
        name = categoryName,
        parentId = parentId,
        iconUrl = null,
        description = null,
        order = 0
    )
}