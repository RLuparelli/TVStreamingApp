package com.tvstreaming.app.ui.screens.animation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.tvstreaming.app.ui.screens.base.BaseContentScreen

/**
 * Tela de Desenhos/Animações usando arquitetura componentizada
 * Zero duplicação de código - 100% DRY
 */
@Composable
fun AnimationScreen(
    onNavigateBack: () -> Unit = {},
    onAnimationClick: (String) -> Unit = {},
    viewModel: AnimationViewModel = hiltViewModel()
) {
    BaseContentScreen(
        viewModel = viewModel,
        onNavigateBack = onNavigateBack,
        onContentClick = onAnimationClick
    )
}