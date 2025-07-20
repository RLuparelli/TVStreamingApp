package com.tvstreaming.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

data class FeaturedContent(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val rating: String? = null,
    val year: String? = null,
    val duration: String? = null
)

@Composable
fun FeaturedCarousel(
    featuredItems: List<FeaturedContent>,
    onItemClick: (FeaturedContent) -> Unit,
    modifier: Modifier = Modifier,
    isTV: Boolean = false
) {
    if (featuredItems.isEmpty()) return
    
    var currentIndex by remember { mutableStateOf(0) }
    val transition = updateTransition(targetState = currentIndex, label = "carousel")
    
    // Auto-scroll
    LaunchedEffect(featuredItems) {
        while (true) {
            delay(5000) // Change slide every 5 seconds
            currentIndex = (currentIndex + 1) % featuredItems.size
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(if (isTV) 400.dp else 250.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        // Background Image with Crossfade
        featuredItems.forEachIndexed { index, item ->
            val alpha by transition.animateFloat(
                transitionSpec = { tween(600) },
                label = "alpha"
            ) { state ->
                if (state == index) 1f else 0f
            }
            
            if (alpha > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { this.alpha = alpha }
                ) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .drawWithCache {
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.5f),
                                                Color.Black.copy(alpha = 0.9f)
                                            ),
                                            startY = size.height * 0.3f,
                                            endY = size.height
                                        )
                                    )
                                }
                            }
                    )
                    
                    // Content Overlay
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(if (isTV) 48.dp else 24.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        AnimatedVisibility(
                            visible = alpha > 0.5f,
                            enter = slideInVertically { it } + fadeIn(),
                            exit = slideOutVertically { -it } + fadeOut()
                        ) {
                            Column {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontSize = if (isTV) 40.sp else 28.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.White
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Metadata
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    item.rating?.let {
                                        Chip(
                                            text = it,
                                            backgroundColor = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    item.year?.let {
                                        Text(
                                            text = it,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                    }
                                    item.duration?.let {
                                        Text(
                                            text = it,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Text(
                                    text = item.description,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White.copy(alpha = 0.9f),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                
                                Spacer(modifier = Modifier.height(20.dp))
                                
                                // Play Button
                                Button(
                                    onClick = { onItemClick(item) },
                                    modifier = Modifier.height(if (isTV) 56.dp else 48.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Assistir",
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Assistir Agora",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontSize = if (isTV) 18.sp else 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Page Indicators
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(if (isTV) 48.dp else 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            featuredItems.indices.forEach { index ->
                val width by transition.animateDp(
                    transitionSpec = { spring(dampingRatio = 0.9f) },
                    label = "indicatorWidth"
                ) { state ->
                    if (state == index) 24.dp else 8.dp
                }
                
                Box(
                    modifier = Modifier
                        .height(4.dp)
                        .width(width)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (currentIndex == index) Color.White
                            else Color.White.copy(alpha = 0.5f)
                        )
                        .clickable { currentIndex = index }
                )
            }
        }
    }
}

@Composable
private fun Chip(
    text: String,
    backgroundColor: Color,
    textColor: Color = Color.White
) {
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}