package com.tvstreaming.app.ui.screens.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tvstreaming.app.core.repositories.MediaRepository
import com.tvstreaming.app.core.utils.Resource
import com.tvstreaming.app.models.ChannelCategory
import com.tvstreaming.app.models.MediaContent
import com.tvstreaming.app.models.MediaType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel base abstrato para todas as telas de conteúdo
 * Refatorado para eliminar duplicação de código
 */
abstract class BaseContentViewModel(
    protected val mediaRepository: MediaRepository
) : ViewModel() {
    
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
    
    /**
     * Inicializa o ViewModel após injeção de dependências
     */
    protected fun initializeViewModel() {
        loadCategories()
        loadContent()
    }
    
    private fun loadCategories() {
        viewModelScope.launch {
            mediaRepository.getCategories(mediaType).collect { result ->
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
                mediaRepository.getFeaturedContent(mediaType).collect { result ->
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
                
                if (categoryId != null) {
                    // Carrega categoria específica
                    mediaRepository.getContentByCategory(mediaType, categoryId).collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                val categoryName = _uiState.value.selectedCategory?.name ?: categoryId
                                val content = result.data ?: emptyList()
                                _uiState.update { state ->
                                    state.copy(
                                        contentByCategory = mapOf(categoryName to content),
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
                } else {
                    // Carrega todas as categorias
                    mediaRepository.getAllContentByCategories(mediaType).collect { result ->
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