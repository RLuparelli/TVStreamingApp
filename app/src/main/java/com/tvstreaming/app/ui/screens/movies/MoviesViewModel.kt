package com.tvstreaming.app.ui.screens.movies

import com.tvstreaming.app.core.repositories.MediaRepository
import com.tvstreaming.app.models.MediaType
import com.tvstreaming.app.ui.screens.base.BaseContentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel simplificado para tela de Filmes
 * Agora usa MediaRepository genérico e herda toda lógica do BaseContentViewModel
 */
@HiltViewModel
class MoviesViewModel @Inject constructor(
    mediaRepository: MediaRepository
) : BaseContentViewModel(mediaRepository) {
    
    override val mediaType: MediaType = MediaType.MOVIE
    override val screenTitle: String = "Filmes"
    
    init {
        initializeViewModel()
    }
}