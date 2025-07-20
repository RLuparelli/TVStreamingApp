package com.tvstreaming.app.ui.screens.series

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.tvstreaming.app.ui.screens.base.BaseContentScreen

/**
 * Tela de Séries usando arquitetura componentizada
 * Zero duplicação de código - reutiliza 100% BaseContentScreen
 */
@Composable
fun SeriesScreen(
    onNavigateBack: () -> Unit = {},
    onSeriesClick: (String) -> Unit = {},
    viewModel: SeriesViewModel = hiltViewModel()
) {
    BaseContentScreen(
        viewModel = viewModel,
        onNavigateBack = onNavigateBack,
        onContentClick = onSeriesClick
    )
}