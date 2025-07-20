package com.tvstreaming.app.ui.screens.channels.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tvstreaming.app.models.Channel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Composable
fun ChannelsList(
    channels: List<Channel>,
    selectedChannel: Channel?,
    searchQuery: String,
    onChannelSelected: (Channel) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onFavoriteToggle: (Channel) -> Unit,
    onLoadMore: () -> Unit,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Search Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                placeholder = { Text("Buscar canais...", color = Color.White.copy(alpha = 0.6f)) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color.White.copy(alpha = 0.6f)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChanged("") }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Limpar busca",
                                tint = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
            
            // Channels List
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = channels,
                    key = { it.id }
                ) { channel ->
                    ChannelItem(
                        channel = channel,
                        isSelected = channel.id == selectedChannel?.id,
                        onChannelSelected = { onChannelSelected(channel) },
                        onFavoriteToggle = { onFavoriteToggle(channel) }
                    )
                }
                
                // Load more indicator
                if (isLoading && channels.isNotEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                
                // Error item
                error?.let {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(onClick = onRetry) {
                                    Text("Tentar novamente")
                                }
                            }
                        }
                    }
                }
            }
            
            // Load more when reaching end
            LaunchedEffect(listState) {
                snapshotFlow { 
                    listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index 
                }
                .distinctUntilChanged()
                .collect { lastIndex ->
                    if (lastIndex != null && lastIndex >= channels.size - 3 && !isLoading) {
                        onLoadMore()
                    }
                }
            }
        }
    }
}

@Composable
private fun ChannelItem(
    channel: Channel,
    isSelected: Boolean,
    onChannelSelected: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChannelSelected() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            } else {
                Color.White.copy(alpha = 0.1f)
            }
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Channel Logo
            AsyncImage(
                model = channel.logo,
                contentDescription = channel.name,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Channel Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${channel.number}. ${channel.name}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (channel.isHD) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Text(
                                text = "HD",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                
                Text(
                    text = channel.currentProgram,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Favorite Button
            IconButton(
                onClick = onFavoriteToggle,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (channel.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (channel.isFavorite) "Remover dos favoritos" else "Adicionar aos favoritos",
                    tint = if (channel.isFavorite) Color.Red else Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}