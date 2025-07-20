package com.tvstreaming.app.ui.screens.movies

import com.tvstreaming.app.core.repositories.MoviesRepository
import com.tvstreaming.app.core.utils.Resource
import com.tvstreaming.app.models.ChannelCategory
import com.tvstreaming.app.models.MediaContent
import com.tvstreaming.app.models.MediaType
import com.tvstreaming.app.models.toMediaContent
import com.tvstreaming.app.ui.screens.base.BaseContentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository
) : BaseContentViewModel() {
    
    override val mediaType: MediaType = MediaType.MOVIE
    override val screenTitle: String = "Filmes"
    
    init {
        initializeViewModel()
    }
    
    override suspend fun fetchCategories(): Flow<Resource<List<ChannelCategory>>> {
        return moviesRepository.getMovieCategories()
    }
    
    override suspend fun fetchFeaturedContent(): Flow<Resource<MediaContent?>> {
        return moviesRepository.getFeaturedMovie().map { result ->
            when (result) {
                is Resource.Success -> Resource.Success(result.data?.toMediaContent())
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
                moviesRepository.getMoviesByCategory(categoryId).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            val categoryName = uiState.value.selectedCategory?.name ?: categoryId
                            val movies = result.data?.map { it.toMediaContent() } ?: emptyList()
                            emit(Resource.Success(mapOf(categoryName to movies)))
                        }
                        is Resource.Error -> emit(Resource.Error(result.error ?: Exception("Unknown error"), result.message ?: "Unknown error"))
                        is Resource.Loading -> emit(Resource.Loading())
                    }
                }
            } else {
                // Carregar todas as categorias
                val allMovies = mutableMapOf<String, List<MediaContent>>()
                val categories = listOf("action", "comedy", "drama", "horror", "romance", "scifi")
                val categoryNames = mapOf(
                    "action" to "Ação",
                    "comedy" to "Comédia",
                    "drama" to "Drama",
                    "horror" to "Terror",
                    "romance" to "Romance",
                    "scifi" to "Ficção Científica"
                )
                
                for (categoryId in categories) {
                    moviesRepository.getMoviesByCategory(categoryId).collect { result ->
                        if (result is Resource.Success && !result.data.isNullOrEmpty()) {
                            val categoryName = categoryNames[categoryId] ?: categoryId
                            allMovies[categoryName] = result.data.map { it.toMediaContent() }
                        }
                    }
                }
                
                emit(Resource.Success(allMovies))
            }
        }
    }
}