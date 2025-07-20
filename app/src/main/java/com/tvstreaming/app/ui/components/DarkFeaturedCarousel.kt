package com.tvstreaming.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun DarkFeaturedCarousel(
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
            delay(5000)
            currentIndex = (currentIndex + 1) % featuredItems.size
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(if (isTV) 400.dp else 250.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black.copy(alpha = 0.5f))
    ) {
        // Background Images
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
                    // Placeholder for image - in production would use AsyncImage
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color.Gray.copy(alpha = 0.3f),
                                        Color.Black
                                    )
                                )
                            )
                            .drawWithCache {
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.4f),
                                                Color.Black.copy(alpha = 0.95f)
                                            ),
                                            startY = size.height * 0.4f,
                                            endY = size.height
                                        )
                                    )
                                }
                            }
                    ) {
                        // Placeholder content
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.headlineLarge,
                                color = Color.White.copy(alpha = 0.3f)
                            )
                        }
                    }
                    
                    // Content Overlay
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                horizontal = if (isTV) 48.dp else 24.dp,
                                vertical = if (isTV) 40.dp else 24.dp
                            ),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        AnimatedVisibility(
                            visible = alpha > 0.5f,
                            enter = slideInVertically { it } + fadeIn(),
                            exit = slideOutVertically { -it } + fadeOut()
                        ) {
                            Column {
                                // Title
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontSize = if (isTV) 36.sp else 24.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.White
                                )
                                
                                // Metadata
                                Row(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    item.rating?.let {
                                        MetadataChip(text = "★ $it", color = Color(0xFFFBBF24))
                                    }
                                    item.year?.let {
                                        MetadataChip(text = it)
                                    }
                                    item.duration?.let {
                                        MetadataChip(text = it)
                                    }
                                }
                                
                                // Description
                                Text(
                                    text = item.description,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White.copy(alpha = 0.8f),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                
                                Spacer(modifier = Modifier.height(20.dp))
                                
                                // Play Button
                                PlayButton(
                                    onClick = { onItemClick(item) },
                                    isTV = isTV
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Minimal Page Indicators
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    horizontal = if (isTV) 48.dp else 24.dp,
                    vertical = 16.dp
                ),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            featuredItems.indices.forEach { index ->
                val isActive = currentIndex == index
                Box(
                    modifier = Modifier
                        .size(if (isActive) 24.dp else 8.dp, 3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (isActive) Color.White 
                            else Color.White.copy(alpha = 0.3f)
                        )
                        .clickable { currentIndex = index }
                )
            }
        }
    }
}

@Composable
private fun MetadataChip(
    text: String,
    color: Color = Color.White.copy(alpha = 0.6f)
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = color,
        modifier = Modifier.padding(end = 8.dp)
    )
}

@Composable
private fun PlayButton(
    onClick: () -> Unit,
    isTV: Boolean
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    Surface(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = Modifier
            .scale(scale)
            .height(if (isTV) 56.dp else 48.dp),
        shape = RoundedCornerShape(28.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Assistir",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Assistir",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = if (isTV) 18.sp else 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.Black
            )
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}