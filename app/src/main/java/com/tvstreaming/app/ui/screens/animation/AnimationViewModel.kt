package com.tvstreaming.app.ui.screens.animation

import com.tvstreaming.app.core.repositories.AnimationRepository
import com.tvstreaming.app.core.utils.Resource
import com.tvstreaming.app.models.ChannelCategory
import com.tvstreaming.app.models.MediaContent
import com.tvstreaming.app.models.MediaType
import com.tvstreaming.app.ui.screens.base.BaseContentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class AnimationViewModel @Inject constructor(
    private val animationRepository: AnimationRepository
) : BaseContentViewModel() {
    
    override val mediaType: MediaType = MediaType.ANIMATION
    override val screenTitle: String = "Desenhos"
    
    init {
        initializeViewModel()
    }
    
    override suspend fun fetchCategories(): Flow<Resource<List<ChannelCategory>>> {
        return animationRepository.getAnimationCategories()
    }
    
    override suspend fun fetchFeaturedContent(): Flow<Resource<MediaContent?>> {
        return animationRepository.getFeaturedAnimation().map { result ->
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
                animationRepository.getAnimationsByCategory(categoryId).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            val categoryName = uiState.value.selectedCategory?.name ?: categoryId
                            val animations = result.data ?: emptyList()
                            emit(Resource.Success(mapOf(categoryName to animations)))
                        }
                        is Resource.Error -> emit(Resource.Error(result.error ?: Exception("Unknown error"), result.message ?: "Unknown error"))
                        is Resource.Loading -> emit(Resource.Loading())
                    }
                }
            } else {
                // Carregar todas as categorias
                val allAnimations = mutableMapOf<String, List<MediaContent>>()
                val categories = listOf("disney", "pixar", "dreamworks", "anime", "classic", "educational")
                val categoryNames = mapOf(
                    "disney" to "Disney",
                    "pixar" to "Pixar",
                    "dreamworks" to "DreamWorks",
                    "anime" to "Anime",
                    "classic" to "Clássicos",
                    "educational" to "Educativo"
                )
                
                for (categoryId in categories) {
                    animationRepository.getAnimationsByCategory(categoryId).collect { result ->
                        if (result is Resource.Success && !result.data.isNullOrEmpty()) {
                            val categoryName = categoryNames[categoryId] ?: categoryId
                            allAnimations[categoryName] = result.data
                        }
                    }
                }
                
                emit(Resource.Success(allAnimations))
            }
        }
    }
}