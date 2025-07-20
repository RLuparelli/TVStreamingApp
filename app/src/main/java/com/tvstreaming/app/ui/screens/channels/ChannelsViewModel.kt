package com.tvstreaming.app.ui.screens.channels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tvstreaming.app.core.utils.Resource
import com.tvstreaming.app.core.repositories.ChannelRepository
import com.tvstreaming.app.models.Channel
import com.tvstreaming.app.models.ChannelCategory
import com.tvstreaming.app.models.ChannelDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelsViewModel @Inject constructor(
    private val channelRepository: ChannelRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ChannelsUiState())
    val uiState: StateFlow<ChannelsUiState> = _uiState.asStateFlow()
    
    private var searchJob: Job? = null
    private var currentPage = 1
    private var hasMorePages = true
    
    init {
        loadCategories()
        loadChannels()
    }
    
    private fun loadCategories() {
        viewModelScope.launch {
            channelRepository.getChannelCategories().collect { result ->
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
    
    fun loadChannels(reset: Boolean = false) {
        if (reset) {
            currentPage = 1
            hasMorePages = true
        }
        
        if (!hasMorePages && !reset) return
        
        viewModelScope.launch {
            channelRepository.getChannels(
                category = _uiState.value.selectedCategory?.id,
                search = _uiState.value.searchQuery.ifEmpty { null },
                page = currentPage
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val newChannels = result.data?.channels ?: emptyList()
                        hasMorePages = currentPage < (result.data?.totalPages ?: 1)
                        
                        _uiState.update { state ->
                            state.copy(
                                channels = if (reset) newChannels else state.channels + newChannels,
                                filteredChannels = if (reset) newChannels else state.filteredChannels + newChannels,
                                isLoading = false,
                                error = null
                            )
                        }
                        
                        currentPage++
                        
                        // Selecionar o primeiro canal automaticamente se nenhum estiver selecionado
                        if (_uiState.value.selectedChannel == null && newChannels.isNotEmpty()) {
                            selectChannel(newChannels.first())
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
    
    fun selectChannel(channel: Channel) {
        _uiState.update { state ->
            state.copy(selectedChannel = channel)
        }
        
        // Buscar detalhes do canal
        viewModelScope.launch {
            channelRepository.getChannelDetails(channel.id).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                selectedChannelDetails = result.data,
                                detailsLoading = false
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { state ->
                            state.copy(
                                detailsError = result.message,
                                detailsLoading = false
                            )
                        }
                    }
                    is Resource.Loading -> {
                        _uiState.update { state ->
                            state.copy(detailsLoading = true)
                        }
                    }
                }
            }
        }
    }
    
    fun selectCategory(category: ChannelCategory?) {
        if (_uiState.value.selectedCategory?.id == category?.id) {
            // Desmarcar se clicar na mesma categoria
            _uiState.update { state ->
                state.copy(selectedCategory = null)
            }
        } else {
            _uiState.update { state ->
                state.copy(selectedCategory = category)
            }
        }
        loadChannels(reset = true)
    }
    
    fun updateSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(searchQuery = query)
        }
        
        // Cancelar busca anterior
        searchJob?.cancel()
        
        // Debounce da busca
        searchJob = viewModelScope.launch {
            delay(500) // Aguardar 500ms antes de buscar
            loadChannels(reset = true)
        }
    }
    
    fun toggleFavorite(channel: Channel) {
        viewModelScope.launch {
            val result = channelRepository.toggleFavorite(channel.id)
            if (result is Resource.Success) {
                // Atualizar o canal na lista
                _uiState.update { state ->
                    state.copy(
                        channels = state.channels.map { ch ->
                            if (ch.id == channel.id) {
                                ch.copy(isFavorite = !ch.isFavorite)
                            } else ch
                        },
                        filteredChannels = state.filteredChannels.map { ch ->
                            if (ch.id == channel.id) {
                                ch.copy(isFavorite = !ch.isFavorite)
                            } else ch
                        }
                    )
                }
            }
        }
    }
    
    fun loadMoreChannels() {
        if (!_uiState.value.isLoading) {
            loadChannels(reset = false)
        }
    }
    
    fun retry() {
        loadCategories()
        loadChannels(reset = true)
    }
}

// UI State
data class ChannelsUiState(
    val channels: List<Channel> = emptyList(),
    val filteredChannels: List<Channel> = emptyList(),
    val categories: List<ChannelCategory> = emptyList(),
    val selectedChannel: Channel? = null,
    val selectedChannelDetails: ChannelDetails? = null,
    val selectedCategory: ChannelCategory? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val categoriesLoading: Boolean = false,
    val detailsLoading: Boolean = false,
    val error: String? = null,
    val detailsError: String? = null
)