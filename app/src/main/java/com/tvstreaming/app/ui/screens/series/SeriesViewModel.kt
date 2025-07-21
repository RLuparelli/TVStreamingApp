package com.tvstreaming.app.ui.screens.series

import com.tvstreaming.app.core.repositories.MediaRepository
import com.tvstreaming.app.models.MediaType
import com.tvstreaming.app.ui.screens.base.BaseContentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel simplificado para tela de Séries
 * Agora usa MediaRepository genérico e herda toda lógica do BaseContentViewModel
 */
@HiltViewModel
class SeriesViewModel @Inject constructor(
    mediaRepository: MediaRepository
) : BaseContentViewModel(mediaRepository) {
    
    override val mediaType: MediaType = MediaType.SERIES
    override val screenTitle: String = "Séries"
    
    init {
        initializeViewModel()
    }
}