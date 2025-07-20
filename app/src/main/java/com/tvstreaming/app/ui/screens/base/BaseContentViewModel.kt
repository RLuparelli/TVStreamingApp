package com.tvstreaming.app.ui.screens.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tvstreaming.app.core.utils.Resource
import com.tvstreaming.app.models.ChannelCategory
import com.tvstreaming.app.models.MediaContent
import com.tvstreaming.app.models.MediaType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel base abstrato para todas as telas de conteúdo
 * Seguindo Open/Closed Principle e DRY
 */
abstract class BaseContentViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(ContentUiState())
    val uiState: StateFlow<ContentUiState> = _uiState.asStateFlow()
    
    /**
     * Tipo de mídia que este ViewModel gerencia
     */
    abstract val mediaType: MediaType
    
    /**
     * Título da tela
     */
    abstract val screenTitle: String
    
    // Remove init block to avoid initialization issues
    
    /**
     * Call this method from the child ViewModels after all dependencies are injected
     */
    protected fun initializeViewModel() {
        loadCategories()
        loadContent()
    }
    
    /**
     * Carrega categorias específicas do tipo de conteúdo
     */
    protected abstract suspend fun fetchCategories(): Flow<Resource<List<ChannelCategory>>>
    
    /**
     * Carrega conteúdo em destaque
     */
    protected abstract suspend fun fetchFeaturedContent(): Flow<Resource<MediaContent?>>
    
    /**
     * Carrega conteúdo por categoria
     */
    protected abstract suspend fun fetchContentByCategory(categoryId: String?): Flow<Resource<Map<String, List<MediaContent>>>>
    
    private fun loadCategories() {
        viewModelScope.launch {
            fetchCategories().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                categories = result.data ?: emptyList(),
                                categoriesLoading = false
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { state ->
                            state.copy(
                                error = result.message,
                                categoriesLoading = false
                            )
                        }
                    }
                    is Resource.Loading -> {
                        _uiState.update { state ->
                            state.copy(categoriesLoading = true)
                        }
                    }
                }
            }
        }
    }
    
    fun loadContent() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Carrega conteúdo em destaque
            launch {
                fetchFeaturedContent().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _uiState.update { state ->
                                state.copy(featuredContent = result.data)
                            }
                        }
                        else -> {}
                    }
                }
            }
            
            // Carrega conteúdo por categoria
            launch {
                val categoryId = _uiState.value.selectedCategory?.id
                
                fetchContentByCategory(categoryId).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _uiState.update { state ->
                                state.copy(
                                    contentByCategory = result.data ?: emptyMap(),
                                    isLoading = false,
                                    error = null
                                )
                            }
                        }
                        is Resource.Error -> {
                            _uiState.update { state ->
                                state.copy(
                                    error = result.message,
                                    isLoading = false
                                )
                            }
                        }
                        is Resource.Loading -> {
                            _uiState.update { state ->
                                state.copy(isLoading = true)
                            }
                        }
                    }
                }
            }
        }
    }
    
    fun selectCategory(category: ChannelCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
        loadContent()
    }
    
    fun retry() {
        loadContent()
    }
}

/**
 * Estado UI genérico para todas as telas de conteúdo
 */
data class ContentUiState(
    val categories: List<ChannelCategory> = emptyList(),
    val selectedCategory: ChannelCategory? = null,
    val contentByCategory: Map<String, List<MediaContent>> = emptyMap(),
    val featuredContent: MediaContent? = null,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val categoriesLoading: Boolean = false,
    val error: String? = null
)