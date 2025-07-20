package com.tvstreaming.app.ui.screens.movies

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.tvstreaming.app.ui.screens.base.BaseContentScreen

/**
 * Tela de Filmes usando arquitetura componentizada
 * Implementação mínima seguindo DRY - reutiliza BaseContentScreen
 */
@Composable
fun MoviesScreen(
    onNavigateBack: () -> Unit = {},
    onMovieClick: (String) -> Unit = {},
    viewModel: MoviesViewModel = hiltViewModel()
) {
    BaseContentScreen(
        viewModel = viewModel,
        onNavigateBack = onNavigateBack,
        onContentClick = onMovieClick
    )
}