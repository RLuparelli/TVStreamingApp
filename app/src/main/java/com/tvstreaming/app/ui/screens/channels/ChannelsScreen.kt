package com.tvstreaming.app.ui.screens.channels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.platform.LocalConfiguration
import com.tvstreaming.app.ui.components.AnimatedSpaceBackground
import com.tvstreaming.app.ui.screens.channels.components.*
import com.tvstreaming.app.ui.theme.isTelevision

@Composable
fun ChannelsScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: ChannelsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val isTV = configuration.isTelevision()
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val screenWidthDp = configuration.screenWidthDp
    val isCompact = screenWidthDp < 600
    
    // Determinar se deve usar layout em duas colunas
    val useTwoColumnLayout = isTV || (isLandscape && !isCompact)
    
    // Estado para controlar o dialog do player no mobile
    var showPlayerDialog by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(if (!isTV) Modifier.safeDrawingPadding() else Modifier)
    ) {
        // Background
        AnimatedSpaceBackground()
        
        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        ) {
            // Header with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color.Black.copy(alpha = 0.6f),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "Canais de TV",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Category Filter (SubHeader)
            CategoryFilter(
                categories = uiState.categories,
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = viewModel::selectCategory,
                isLoading = uiState.categoriesLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 0.dp)
            )
            
            // Main Content Area - Responsive Layout
            if (useTwoColumnLayout) {
                // Layout em duas colunas para TV e tablets em paisagem
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Left Side: Channels List with Search
                    Box(
                        modifier = Modifier
                            .weight(0.35f)
                            .fillMaxHeight()
                            .padding(end = 8.dp)
                    ) {
                        ChannelsList(
                            channels = uiState.filteredChannels,
                            selectedChannel = uiState.selectedChannel,
                            searchQuery = uiState.searchQuery,
                            onChannelSelected = viewModel::selectChannel,
                            onSearchQueryChanged = viewModel::updateSearchQuery,
                            onFavoriteToggle = viewModel::toggleFavorite,
                            onLoadMore = viewModel::loadMoreChannels,
                            isLoading = uiState.isLoading,
                            error = uiState.error,
                            onRetry = viewModel::retry
                        )
                    }
                    
                    // Right Side: Player and Details
                    Column(
                        modifier = Modifier
                            .weight(0.65f)
                            .fillMaxHeight()
                            .padding(start = 8.dp)
                    ) {
                        // Video Player Preview (Upper Right)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.6f),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Black.copy(alpha = 0.7f)
                            )
                        ) {
                            val selectedChannel = uiState.selectedChannel
                            if (selectedChannel != null) {
                                VideoPlayer(
                                    channel = selectedChannel,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Selecione um canal",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Channel Details (Lower Right)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.4f),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Black.copy(alpha = 0.7f)
                            )
                        ) {
                            val selectedChannel = uiState.selectedChannel
                            if (selectedChannel != null) {
                                ChannelDetails(
                                    channel = selectedChannel,
                                    channelDetails = uiState.selectedChannelDetails,
                                    isLoading = uiState.detailsLoading,
                                    error = uiState.detailsError,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                )
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Nenhum canal selecionado",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Layout em lista para celulares em retrato
                ChannelsListMobile(
                    channels = uiState.filteredChannels,
                    selectedChannel = uiState.selectedChannel,
                    searchQuery = uiState.searchQuery,
                    onChannelSelected = { channel ->
                        viewModel.selectChannel(channel)
                        if (!useTwoColumnLayout) {
                            showPlayerDialog = true
                        }
                    },
                    onSearchQueryChanged = viewModel::updateSearchQuery,
                    onFavoriteToggle = viewModel::toggleFavorite,
                    onLoadMore = viewModel::loadMoreChannels,
                    isLoading = uiState.isLoading,
                    error = uiState.error,
                    onRetry = viewModel::retry,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                )
            }
        }
        
        // Loading overlay
        if (uiState.isLoading && uiState.channels.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Carregando canais...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
            }
        }
        
        // Error state
        uiState.error?.let { error ->
            if (uiState.channels.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Erro ao carregar canais",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = viewModel::retry,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Tentar Novamente")
                        }
                    }
                }
            }
        }
        
        // Player Dialog para mobile
        if (showPlayerDialog && uiState.selectedChannel != null && !useTwoColumnLayout) {
            ChannelPlayerDialog(
                channel = uiState.selectedChannel!!,
                channelDetails = uiState.selectedChannelDetails,
                onDismiss = { showPlayerDialog = false }
            )
        }
    }
}