package com.tvstreaming.app.ui.screens.animation

import com.tvstreaming.app.core.repositories.MediaRepository
import com.tvstreaming.app.models.MediaType
import com.tvstreaming.app.ui.screens.base.BaseContentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel simplificado para tela de Animações/Desenhos
 * Agora usa MediaRepository genérico e herda toda lógica do BaseContentViewModel
 */
@HiltViewModel
class AnimationViewModel @Inject constructor(
    mediaRepository: MediaRepository
) : BaseContentViewModel(mediaRepository) {
    
    override val mediaType: MediaType = MediaType.ANIMATION
    override val screenTitle: String = "Desenhos"
    
    init {
        initializeViewModel()
    }
}