package com.tvstreaming.app.core.repositories

import com.tvstreaming.app.core.api.ApiService
import com.tvstreaming.app.core.storage.dao.ContentDao
import com.tvstreaming.app.core.storage.entities.CategoryEntity
import com.tvstreaming.app.core.storage.entities.toEntity
import com.tvstreaming.app.core.utils.Resource
import com.tvstreaming.app.core.utils.safeApiCall
import com.tvstreaming.app.core.utils.safeCall
import com.tvstreaming.app.models.Category
import com.tvstreaming.app.models.Content
import com.tvstreaming.app.models.ContentItem
import com.tvstreaming.app.models.ContentDetails
import com.tvstreaming.app.models.toContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentRepository @Inject constructor(
    private val apiService: ApiService,
    private val contentDao: ContentDao
) {
    
    fun getCategories(): Flow<Resource<List<Category>>> = flow {
        emit(Resource.Loading())
        
        // Try to get from cache first
        val cachedCategories = contentDao.getAllCategories()
        if (cachedCategories.isNotEmpty()) {
            emit(Resource.Success(cachedCategories.map { it.toCategory() }))
        } else {
            // Return empty list if no cache - don't call API for now
            emit(Resource.Success(emptyList()))
        }
        
        // TODO: Re-enable API calls after fixing connectivity
        // Fetch fresh data from API
        /*
        when (val result = safeCall { apiService.getCategories() }) {
            is Resource.Success -> {
                result.data?.let { categories ->
                    // Update cache
                    contentDao.insertCategories(categories.map { it.toEntity() })
                    emit(Resource.Success(categories))
                }
            }
            is Resource.Error -> {
                if (cachedCategories.isEmpty()) {
                    emit(Resource.Error(result.error ?: Exception("Unknown error")))
                }
                // If we have cached data, keep showing it even if API fails
            }
            is Resource.Loading -> {
                // Already emitted loading
            }
        }
        */
    }
    
    suspend fun getCategoryContent(categoryId: String, page: Int = 1): Resource<List<Content>> {
        return when (val result = safeCall { 
            apiService.getCategoryContent(categoryId, page) 
        }) {
            is Resource.Success -> {
                result.data?.let { response ->
                    Resource.Success(response.contents.map { it.toContent() })
                } ?: Resource.Error(Exception("Empty response"))
            }
            is Resource.Error -> Resource.Error(result.error ?: Exception("Unknown error"), result.message ?: "Unknown error")
            is Resource.Loading -> Resource.Loading()
        }
    }
    
    suspend fun getContentDetails(contentId: String): Resource<ContentDetails> {
        return when (val result = safeCall { apiService.getContentDetails(contentId) }) {
            is Resource.Success -> {
                result.data?.let { content ->
                    Resource.Success(content)
                } ?: Resource.Error(Exception("Content not found"))
            }
            is Resource.Error -> result
            is Resource.Loading -> result
        }
    }
    
    suspend fun getContinueWatching(): Resource<List<Content>> {
        // TODO: Implement with local database
        return Resource.Success(emptyList())
    }
}