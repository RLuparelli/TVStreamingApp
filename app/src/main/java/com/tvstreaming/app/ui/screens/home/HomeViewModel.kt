package com.tvstreaming.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tvstreaming.app.core.repositories.ContentRepository
import com.tvstreaming.app.core.utils.Resource
import com.tvstreaming.app.models.Category
import com.tvstreaming.app.models.Content
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val contentRepository: ContentRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        // Temporarily use mock data instead of API
        loadMockCategories()
        // loadCategories()
    }
    
    fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            
            contentRepository.getCategories().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val categories = resource.data ?: emptyList()
                        loadContinueWatching(categories)
                    }
                    is Resource.Error -> {
                        _uiState.value = HomeUiState.Error(
                            resource.message ?: "Failed to load content"
                        )
                    }
                    is Resource.Loading -> {
                        _uiState.value = HomeUiState.Loading
                    }
                }
            }
        }
    }
    
    private suspend fun loadContinueWatching(categories: List<Category>) {
        when (val result = contentRepository.getContinueWatching()) {
            is Resource.Success -> {
                _uiState.value = HomeUiState.Success(
                    categories = categories,
                    continueWatching = result.data
                )
            }
            is Resource.Error -> {
                // Even if continue watching fails, show categories
                _uiState.value = HomeUiState.Success(
                    categories = categories,
                    continueWatching = null
                )
            }
            is Resource.Loading -> {
                // Should not happen for single shot operations
            }
        }
    }
    
    private fun loadMockCategories() {
        _uiState.value = HomeUiState.Success(
            categories = listOf(
                Category(
                    categoryId = "1",
                    categoryName = "Filmes Populares",
                    parentId = 0
                ),
                Category(
                    categoryId = "2",
                    categoryName = "Séries em Alta",
                    parentId = 0
                ),
                Category(
                    categoryId = "3",
                    categoryName = "Documentários",
                    parentId = 0
                ),
                Category(
                    categoryId = "4",
                    categoryName = "Infantil",
                    parentId = 0
                )
            ),
            continueWatching = emptyList()
        )
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val categories: List<Category>,
        val continueWatching: List<Content>?
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}