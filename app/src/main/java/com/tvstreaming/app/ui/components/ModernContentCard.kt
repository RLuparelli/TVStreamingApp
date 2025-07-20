package com.tvstreaming.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// Simple content data class for the component
data class ContentData(
    val id: String,
    val title: String,
    val posterUrl: String,
    val year: String? = null,
    val rating: Float? = null,
    val watchProgress: Float? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernContentCard(
    content: ContentData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isTV: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    
    // Animações
    val scale by animateFloatAsState(
        targetValue = when {
            isFocused || isHovered -> 1.08f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    val cardElevation by animateFloatAsState(
        targetValue = if (isFocused || isHovered) 24f else 8f,
        animationSpec = spring(),
        label = "elevation"
    )
    
    // Parallax effect
    val parallaxOffset by animateFloatAsState(
        targetValue = if (isFocused || isHovered) -10f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "parallax"
    )
    
    Box(
        modifier = modifier
            .scale(scale)
            .graphicsLayer {
                shadowElevation = cardElevation.dp.toPx()
                shape = RoundedCornerShape(16.dp)
                clip = true
            }
    ) {
        Card(
            onClick = onClick,
            modifier = Modifier
                .fillMaxSize()
                .focusable(interactionSource = interactionSource),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            interactionSource = interactionSource
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Background image with parallax
                AsyncImage(
                    model = content.posterUrl,
                    contentDescription = content.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            translationY = parallaxOffset
                        }
                        .drawWithCache {
                            onDrawWithContent {
                                drawContent()
                                // Gradient overlay
                                drawRect(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.8f)
                                        ),
                                        startY = size.height * 0.5f
                                    )
                                )
                            }
                        },
                    contentScale = ContentScale.Crop
                )
                
                // Glass morphic overlay for focused state
                if (isFocused || isHovered) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Color.White.copy(alpha = 0.05f)
                            )
                    ) {
                        // Animated shimmer effect
                        ShimmerOverlay(
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                
                // Content info
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(if (isTV) 16.dp else 12.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    // Play icon for focused state
                    androidx.compose.animation.AnimatedVisibility(
                        visible = isFocused || isHovered,
                        enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.scaleIn(),
                        exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.scaleOut()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayCircle,
                                contentDescription = "Play",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(if (isTV) 56.dp else 48.dp)
                                    .drawBehind {
                                        drawCircle(
                                            color = Color.Black.copy(alpha = 0.3f),
                                            radius = size.width / 2 + 4.dp.toPx(),
                                            center = center
                                        )
                                    }
                            )
                        }
                    }
                    
                    // Title with glass background
                    Surface(
                        color = Color.Black.copy(alpha = if (isFocused || isHovered) 0.7f else 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 8.dp
                            )
                        ) {
                            Text(
                                text = content.title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontSize = if (isTV) 16.sp else 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    shadow = Shadow(
                                        color = Color.Black.copy(alpha = 0.5f),
                                        offset = Offset(0f, 2f),
                                        blurRadius = 4f
                                    )
                                ),
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            
                            // Additional info for focused state
                            androidx.compose.animation.AnimatedVisibility(
                                visible = isFocused || isHovered
                            ) {
                                Row(
                                    modifier = Modifier.padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    content.year?.let { year ->
                                        Text(
                                            text = year,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                    }
                                    content.rating?.let { rating ->
                                        Surface(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = String.format("%.1f", rating),
                                                modifier = Modifier.padding(
                                                    horizontal = 6.dp,
                                                    vertical = 2.dp
                                                ),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Progress indicator if available
                content.watchProgress?.let { progress ->
                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .align(Alignment.BottomCenter),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.White.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ShimmerOverlay(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )
    
    Canvas(modifier = modifier) {
        val shimmerWidth = size.width * 0.4f
        val x = size.width * shimmerOffset
        
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = 0.1f),
                    Color.Transparent
                ),
                startX = x - shimmerWidth / 2,
                endX = x + shimmerWidth / 2
            )
        )
    }
}