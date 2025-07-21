package com.tvstreaming.app.ui.screens.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.platform.LocalConfiguration
import com.tvstreaming.app.ui.components.AnimatedSpaceBackground
import com.tvstreaming.app.ui.components.content.*
import com.tvstreaming.app.ui.screens.channels.components.CategoryFilter
import com.tvstreaming.app.ui.theme.isTelevision

/**
 * Tela base genérica para todos os tipos de conteúdo
 * Seguindo todos os princípios SOLID e DRY
 */
@Composable
fun BaseContentScreen(
    viewModel: BaseContentViewModel,
    onNavigateBack: () -> Unit,
    onContentClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val isTV = configuration.isTelevision()
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .then(if (!isTV) Modifier.safeDrawingPadding() else Modifier)
    ) {
        // Background animado
        AnimatedSpaceBackground()
        
        // Conteúdo principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        ) {
            // Header
            ContentScreenHeader(
                title = viewModel.screenTitle,
                onNavigateBack = onNavigateBack
            )
            
            // Filtro de categorias
            CategoryFilter(
                categories = uiState.categories,
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = viewModel::selectCategory,
                isLoading = uiState.categoriesLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            
            // Conteúdo principal
            ContentBody(
                uiState = uiState,
                onContentClick = onContentClick,
                onRetry = viewModel::retry
            )
        }
    }
}

/**
 * Corpo do conteúdo com estados de carregamento e erro
 */
@Composable
private fun ContentBody(
    uiState: ContentUiState,
    onContentClick: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        // Estado de carregamento
        uiState.isLoading && uiState.contentByCategory.isEmpty() -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        // Estado de erro
        uiState.error != null && uiState.contentByCategory.isEmpty() -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val errorMessage = uiState.error
                    Text(
                        text = errorMessage ?: "Erro desconhecido",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRetry) {
                        Text("Tentar Novamente")
                    }
                }
            }
        }
        
        // Estado de sucesso
        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Conteúdo em destaque
                val featuredContent = uiState.featuredContent
                if (uiState.selectedCategory == null && featuredContent != null) {
                    item {
                        FeaturedContentCard(
                            content = featuredContent,
                            onClick = { onContentClick(featuredContent.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .padding(16.dp)
                        )
                    }
                }
                
                // Conteúdo por categoria
                uiState.contentByCategory.forEach { (category, contents) ->
                    item {
                        ContentCategoryRow(
                            categoryName = category,
                            contents = contents,
                            onContentClick = onContentClick,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
                
                // Indicador de carregamento adicional
                if (uiState.isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}