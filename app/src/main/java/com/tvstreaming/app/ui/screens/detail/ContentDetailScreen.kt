package com.tvstreaming.app.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tvstreaming.app.core.repositories.ContentDetailRepository
import com.tvstreaming.app.models.Episode
import com.tvstreaming.app.ui.components.AnimatedSpaceBackground

@Composable
fun ContentDetailScreen(
    onNavigateBack: () -> Unit,
    onPlayContent: (String) -> Unit,
    viewModel: ContentDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background
        AnimatedSpaceBackground()
        
        // Content
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
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
                        Button(onClick = viewModel::retry) {
                            Text("Tentar Novamente")
                        }
                    }
                }
            }
            uiState.contentDetail != null -> {
                val contentDetail = uiState.contentDetail!!
                ContentDetailLayout(
                    detail = contentDetail,
                    selectedSeason = uiState.selectedSeason,
                    displayedEpisodes = uiState.displayedEpisodes,
                    onNavigateBack = onNavigateBack,
                    onPlayContent = { 
                        onPlayContent(contentDetail.id)
                    },
                    onPlayEpisode = { episodeId ->
                        onPlayContent(episodeId)
                    },
                    onSeasonSelected = viewModel::selectSeason
                )
            }
        }
    }
}

@Composable
private fun ContentDetailLayout(
    detail: ContentDetailRepository.ContentDetailInfo,
    selectedSeason: Int,
    displayedEpisodes: List<Episode>?,
    onNavigateBack: () -> Unit,
    onPlayContent: () -> Unit,
    onPlayEpisode: (String) -> Unit,
    onSeasonSelected: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
    ) {
        // Header with trailer/thumbnail
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (detail.isMovie) 500.dp else 400.dp)
            ) {
                // Backdrop image
                AsyncImage(
                    model = detail.backdropUrl,
                    contentDescription = detail.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Black.copy(alpha = 0.95f)
                                ),
                                startY = 100f
                            )
                        )
                )
                
                // Back button
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                        .size(48.dp)
                        .background(
                            Color.Black.copy(alpha = 0.6f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White
                    )
                }
                
                // Play button overlay (for trailer)
                if (detail.trailerUrl != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(80.dp)
                            .background(
                                Color.Black.copy(alpha = 0.7f),
                                shape = CircleShape
                            )
                            .clickable { /* TODO: Play trailer */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Assistir Trailer",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }
        }
        
        // Content info
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Title
                Text(
                    text = detail.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Metadata row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rating
                    detail.rating?.let { rating ->
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "★ $rating",
                                color = Color.White,
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                    
                    // Year
                    detail.year?.let { year ->
                        Text(
                            text = year,
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    
                    // Duration
                    detail.duration?.let { duration ->
                        Text(
                            text = if (detail.isMovie) {
                                "${duration.div(60)}h ${duration.rem(60)}min"
                            } else {
                                "$duration min/ep"
                            },
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Play button
                Button(
                    onClick = onPlayContent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Assistir",
                        color = Color.Black,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Description
                Text(
                    text = detail.description,
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                // Additional info
                detail.genre?.let { genre ->
                    Text(
                        text = "Gênero: $genre",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                
                detail.director?.let { director ->
                    Text(
                        text = "Diretor: $director",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                detail.cast?.let { cast ->
                    Text(
                        text = "Elenco: $cast",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
        
        // Episodes section for series
        if (!detail.isMovie && detail.seasons != null) {
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Episódios",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Season selector
                    if ((detail.seasons?.size ?: 0) > 1) {
                        Spacer(modifier = Modifier.height(16.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            detail.seasons?.let { seasons ->
                                items(seasons) { season ->
                                    FilterChip(
                                        selected = season.seasonNumber == selectedSeason,
                                        onClick = { onSeasonSelected(season.seasonNumber) },
                                        label = {
                                            Text(
                                                text = "Temporada ${season.seasonNumber}",
                                                color = if (season.seasonNumber == selectedSeason) Color.Black else Color.White
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color.White,
                                            containerColor = Color.White.copy(alpha = 0.2f)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Episodes grid
            displayedEpisodes?.let { episodes ->
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                episodes.forEach { episode ->
                    item {
                        EpisodeCard(
                            episode = episode,
                            onClick = { onPlayEpisode(episode.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
        
        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun EpisodeCard(
    episode: Episode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Episode thumbnail
            AsyncImage(
                model = episode.thumbnail,
                contentDescription = episode.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(120.dp)
                    .height(68.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Gray)
            )
            
            // Episode info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${episode.episodeNumber}. ${episode.title}",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                episode.description?.let { desc ->
                    Text(
                        text = desc,
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Text(
                    text = episode.getFormattedDuration(),
                    color = Color.White.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}