package com.tvstreaming.app.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.safeDrawingPadding
import com.tvstreaming.app.ui.components.*
import com.tvstreaming.app.ui.screens.home.components.CategoryRow
import com.tvstreaming.app.ui.theme.isTelevision

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    onNavigateToPlayer: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToCategory: (String) -> Unit = {},
    onNavigateToChannels: () -> Unit = {},
    onNavigateToMovies: () -> Unit = {},
    onNavigateToSeries: () -> Unit = {},
    onNavigateToAnimation: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val isTV = configuration.isTelevision()
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Fundo espacial animado
        AnimatedSpaceBackground()
        
        // Removendo partículas flutuantes por enquanto para melhor visibilidade
        // FloatingParticles(
        //     modifier = Modifier.fillMaxSize(),
        //     particleCount = 30
        // )
        
        // Conteúdo principal
        AnimatedContent(
            targetState = uiState,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith
                fadeOut(animationSpec = tween(300))
            },
            label = "HomeScreenContent"
        ) { state ->
            when (state) {
                is HomeUiState.Loading -> LoadingScreen()
                is HomeUiState.Error -> ErrorScreen(
                    message = state.message,
                    onRetry = { viewModel.loadCategories() }
                )
                is HomeUiState.Success -> HomeContent(
                    state = state,
                    onContentClick = onNavigateToPlayer,
                    onSearchClick = onNavigateToSearch,
                    onCategoryClick = onNavigateToCategory,
                    onChannelsClick = onNavigateToChannels,
                    onMoviesClick = onNavigateToMovies,
                    onSeriesClick = onNavigateToSeries,
                    onAnimationClick = onNavigateToAnimation,
                    isTV = isTV
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState.Success,
    onContentClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onChannelsClick: () -> Unit,
    onMoviesClick: () -> Unit,
    onSeriesClick: () -> Unit,
    onAnimationClick: () -> Unit,
    isTV: Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .then(if (!isTV) Modifier.safeDrawingPadding() else Modifier),
        verticalArrangement = Arrangement.spacedBy(if (isTV) 32.dp else 24.dp)
    ) {
        // Header
        item {
            HomeHeader(
                userName = "Usuário",
                onSearchClick = onSearchClick,
                style = HeaderStyle.PROFESSIONAL,
                isTV = isTV
            )
        }
        
        // Featured Carousel
        item {
            val featuredItems = remember {
                listOf(
                    FeaturedContent(
                        id = "1",
                        title = "Avatar: O Caminho da Água",
                        description = "Jake Sully vive com sua nova família na lua Pandora. Quando uma ameaça familiar retorna, Jake deve trabalhar com Neytiri e o exército dos Na'vi para proteger seu planeta.",
                        imageUrl = "https://image.tmdb.org/t/p/original/t6HIqrRAclMCA60NsSmeqe9RmNV.jpg",
                        rating = "7.8",
                        year = "2022",
                        duration = "3h 12min"
                    ),
                    FeaturedContent(
                        id = "2",
                        title = "The Last of Us",
                        description = "Em um mundo pós-apocalíptico, um sobrevivente endurecido escolta uma adolescente por um país perigoso e desolado.",
                        imageUrl = "https://image.tmdb.org/t/p/original/uKvVjHNqB5VmOrdxqAt2F7J78ED.jpg",
                        rating = "8.7",
                        year = "2023"
                    ),
                    FeaturedContent(
                        id = "3",
                        title = "Oppenheimer",
                        description = "A história do cientista americano J. Robert Oppenheimer e seu papel no desenvolvimento da bomba atômica.",
                        imageUrl = "https://image.tmdb.org/t/p/original/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg",
                        rating = "8.5",
                        year = "2023",
                        duration = "3h"
                    )
                )
            }
            
            FeaturedCarousel(
                featuredItems = featuredItems,
                onItemClick = { onContentClick(it.id) },
                style = CarouselStyle.DARK,
                modifier = Modifier.padding(
                    horizontal = if (isTV) 48.dp else 16.dp
                ),
                isTV = isTV
            )
        }
        
        // Main Categories
        item {
            Column(
                modifier = Modifier.padding(
                    horizontal = if (isTV) 48.dp else 16.dp
                )
            ) {
                Text(
                    text = "Categorias",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                val categories = getDefaultCategories()
                
                if (isTV) {
                    // TV Layout - Horizontal scrolling with focus
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(categories) { category ->
                            CategoryCard(
                                category = category,
                                style = CategoryCardStyle.DARK,
                                onClick = { 
                                    when (category.id) {
                                        "live_tv" -> onChannelsClick()
                                        "movies" -> onMoviesClick()
                                        "series" -> onSeriesClick()
                                        "kids" -> onAnimationClick()
                                        else -> onCategoryClick(category.id)
                                    }
                                },
                                modifier = Modifier
                                    .width(200.dp)
                                    .height(150.dp),
                                isTV = true
                            )
                        }
                    }
                } else {
                    // Mobile Layout - 2 column grid
                    val gridItems = categories.chunked(2)
                    gridItems.forEach { rowItems ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            rowItems.forEach { category ->
                                CategoryCard(
                                    category = category,
                                    style = CategoryCardStyle.DARK,
                                    onClick = { 
                                        when (category.id) {
                                            "live_tv" -> onChannelsClick()
                                            "movies" -> onMoviesClick()
                                            "series" -> onSeriesClick()
                                            "kids" -> onAnimationClick()
                                            else -> onCategoryClick(category.id)
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(140.dp),
                                    isTV = false
                                )
                            }
                            // Add empty space if odd number of items
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
        
        // Continue Watching Section
        state.continueWatching?.let { contents ->
            if (contents.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier.animateContentSize()
                    ) {
                        CategoryRow(
                            title = "Continue Assistindo",
                            contents = contents,
                            onContentClick = onContentClick,
                            isTV = isTV
                        )
                    }
                }
            }
        }
        
        // Dynamic Categories from API
        if (state.categories.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Mais Categorias",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White,
                    modifier = Modifier.padding(
                        horizontal = if (isTV) 48.dp else 16.dp,
                        vertical = 8.dp
                    )
                )
            }
            
            items(state.categories) { category ->
                CategoryRow(
                    title = category.categoryName,
                    contents = emptyList(), // Contents will be loaded on demand
                    onContentClick = onContentClick,
                    isTV = isTV
                )
            }
        }
        
        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(if (isTV) 48.dp else 24.dp))
        }
    }
}