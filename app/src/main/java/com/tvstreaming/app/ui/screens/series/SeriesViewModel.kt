package com.tvstreaming.app.ui.screens.series

import com.tvstreaming.app.core.repositories.SeriesRepository
import com.tvstreaming.app.core.utils.Resource
import com.tvstreaming.app.models.ChannelCategory
import com.tvstreaming.app.models.MediaContent
import com.tvstreaming.app.models.MediaType
import com.tvstreaming.app.ui.screens.base.BaseContentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SeriesViewModel @Inject constructor(
    private val seriesRepository: SeriesRepository
) : BaseContentViewModel() {
    
    override val mediaType: MediaType = MediaType.SERIES
    override val screenTitle: String = "Séries"
    
    init {
        initializeViewModel()
    }
    
    override suspend fun fetchCategories(): Flow<Resource<List<ChannelCategory>>> {
        return seriesRepository.getSeriesCategories()
    }
    
    override suspend fun fetchFeaturedContent(): Flow<Resource<MediaContent?>> {
        return seriesRepository.getFeaturedSeries().map { result ->
            when (result) {
                is Resource.Success -> Resource.Success(result.data)
                is Resource.Error -> Resource.Error(result.error ?: Exception("Unknown error"), result.message ?: "Unknown error")
                is Resource.Loading -> Resource.Loading()
            }
        }
    }
    
    override suspend fun fetchContentByCategory(categoryId: String?): Flow<Resource<Map<String, List<MediaContent>>>> {
        return flow {
            emit(Resource.Loading())
            
            if (categoryId != null) {
                // Carregar categoria específica
                seriesRepository.getSeriesByCategory(categoryId).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            val categoryName = uiState.value.selectedCategory?.name ?: categoryId
                            val series = result.data ?: emptyList()
                            emit(Resource.Success(mapOf(categoryName to series)))
                        }
                        is Resource.Error -> emit(Resource.Error(result.error ?: Exception("Unknown error"), result.message ?: "Unknown error"))
                        is Resource.Loading -> emit(Resource.Loading())
                    }
                }
            } else {
                // Carregar todas as categorias
                val allSeries = mutableMapOf<String, List<MediaContent>>()
                val categories = listOf("drama", "comedy", "action", "scifi", "thriller", "documentary")
                val categoryNames = mapOf(
                    "drama" to "Drama",
                    "comedy" to "Comédia",
                    "action" to "Ação & Aventura",
                    "scifi" to "Ficção Científica",
                    "thriller" to "Suspense",
                    "documentary" to "Documentário"
                )
                
                for (categoryId in categories) {
                    seriesRepository.getSeriesByCategory(categoryId).collect { result ->
                        if (result is Resource.Success && !result.data.isNullOrEmpty()) {
                            val categoryName = categoryNames[categoryId] ?: categoryId
                            allSeries[categoryName] = result.data
                        }
                    }
                }
                
                emit(Resource.Success(allSeries))
            }
        }
    }
}