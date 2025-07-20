package com.tvstreaming.app.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tvstreaming.app.core.repositories.ContentDetailRepository
import com.tvstreaming.app.core.utils.Resource
import com.tvstreaming.app.models.Episode
import com.tvstreaming.app.models.Season
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContentDetailViewModel @Inject constructor(
    private val contentDetailRepository: ContentDetailRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val contentId: String = checkNotNull(savedStateHandle["contentId"])
    
    private val _uiState = MutableStateFlow(ContentDetailUiState())
    val uiState: StateFlow<ContentDetailUiState> = _uiState.asStateFlow()
    
    init {
        loadContentDetail()
    }
    
    private fun loadContentDetail() {
        viewModelScope.launch {
            contentDetailRepository.getContentDetail(contentId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        result.data?.let { detail ->
                            _uiState.update { state ->
                                state.copy(
                                    isLoading = false,
                                    contentDetail = detail,
                                    selectedSeason = detail.seasons?.firstOrNull()?.seasonNumber ?: 1,
                                    error = null
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                error = result.message ?: "Erro ao carregar detalhes"
                            )
                        }
                    }
                }
            }
        }
    }
    
    fun selectSeason(seasonNumber: Int) {
        _uiState.update { state ->
            state.copy(selectedSeason = seasonNumber)
        }
    }
    
    fun playContent() {
        // This will be handled by navigation to player screen
    }
    
    fun playEpisode(episodeId: String) {
        // This will be handled by navigation to player screen
    }
    
    fun retry() {
        loadContentDetail()
    }
}

data class ContentDetailUiState(
    val isLoading: Boolean = false,
    val contentDetail: ContentDetailRepository.ContentDetailInfo? = null,
    val selectedSeason: Int = 1,
    val error: String? = null
) {
    val displayedEpisodes: List<Episode>? 
        get() = contentDetail?.seasons
            ?.find { it.seasonNumber == selectedSeason }
            ?.episodes
}