@file:OptIn(ExperimentalAnimationApi::class)

package com.tvstreaming.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
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
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

enum class CarouselStyle {
    COLORFUL,  // Original FeaturedCarousel style
    DARK,      // DarkFeaturedCarousel style
    MODERN     // ModernFeaturedCarousel style
}

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
    style: CarouselStyle = CarouselStyle.COLORFUL,
    isTV: Boolean = false
) {
    when (style) {
        CarouselStyle.COLORFUL -> ColorfulFeaturedCarousel(
            featuredItems = featuredItems,
            onItemClick = onItemClick,
            modifier = modifier,
            isTV = isTV
        )
        CarouselStyle.DARK -> DarkStyleFeaturedCarousel(
            featuredItems = featuredItems,
            onItemClick = onItemClick,
            modifier = modifier,
            isTV = isTV
        )
        CarouselStyle.MODERN -> ModernStyleFeaturedCarousel(
            featuredItems = featuredItems,
            onItemClick = onItemClick,
            modifier = modifier,
            isTV = isTV
        )
    }
}

@Composable
private fun ColorfulFeaturedCarousel(
    featuredItems: List<FeaturedContent>,
    onItemClick: (FeaturedContent) -> Unit,
    modifier: Modifier = Modifier,
    isTV: Boolean = false
) {
    if (featuredItems.isEmpty()) return
    
    var currentIndex by remember { mutableStateOf(0) }
    val transition = updateTransition(targetState = currentIndex, label = "carousel")
    
    LaunchedEffect(featuredItems) {
        while (true) {
            delay(5000)
            currentIndex = (currentIndex + 1) % featuredItems.size
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(if (isTV) 500.dp else 240.dp)
            .clip(RoundedCornerShape(24.dp))
    ) {
        transition.AnimatedContent(
            transitionSpec = {
                fadeIn(animationSpec = tween(600)) + 
                scaleIn(initialScale = 0.95f, animationSpec = tween(600)) togetherWith
                fadeOut(animationSpec = tween(300)) + 
                scaleOut(targetScale = 1.05f, animationSpec = tween(300))
            }
        ) { index ->
            val item = featuredItems[index]
            FeaturedContentCard(
                content = item,
                onClick = { onItemClick(item) },
                isTV = isTV
            )
        }
        
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            featuredItems.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .size(if (index == currentIndex) 24.dp else 8.dp, 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (index == currentIndex) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                Color.White.copy(alpha = 0.5f)
                        )
                        .clickable { currentIndex = index }
                )
            }
        }
    }
}

@Composable
private fun DarkStyleFeaturedCarousel(
    featuredItems: List<FeaturedContent>,
    onItemClick: (FeaturedContent) -> Unit,
    modifier: Modifier = Modifier,
    isTV: Boolean = false
) {
    if (featuredItems.isEmpty()) return
    
    var currentIndex by remember { mutableStateOf(0) }
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    
    val shimmerTranslateAnim by infiniteTransition.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )
    
    LaunchedEffect(featuredItems) {
        while (true) {
            delay(6000)
            currentIndex = (currentIndex + 1) % featuredItems.size
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(if (isTV) 480.dp else 280.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {
        AnimatedContent(
            targetState = currentIndex,
            transitionSpec = {
                (fadeIn(animationSpec = tween(1000)) + 
                slideInHorizontally(animationSpec = tween(1000)) { it }) togetherWith
                (fadeOut(animationSpec = tween(500)) + 
                slideOutHorizontally(animationSpec = tween(500)) { -it })
            },
            label = "carousel"
        ) { index ->
            val item = featuredItems[index]
            DarkContentCard(
                content = item,
                onClick = { onItemClick(item) },
                shimmerTranslate = shimmerTranslateAnim,
                isTV = isTV
            )
        }
        
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                featuredItems.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .size(if (index == currentIndex) 40.dp else 12.dp, 4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (index == currentIndex) 
                                    Color.White 
                                else 
                                    Color.White.copy(alpha = 0.3f)
                            )
                            .clickable { currentIndex = index }
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernStyleFeaturedCarousel(
    featuredItems: List<FeaturedContent>,
    onItemClick: (FeaturedContent) -> Unit,
    modifier: Modifier = Modifier,
    isTV: Boolean = false
) {
    if (featuredItems.isEmpty()) return
    
    var currentIndex by remember { mutableStateOf(0) }
    val transition = updateTransition(targetState = currentIndex, label = "modern_carousel")
    
    LaunchedEffect(featuredItems) {
        while (true) {
            delay(7000)
            currentIndex = (currentIndex + 1) % featuredItems.size
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(if (isTV) 600.dp else 320.dp)
    ) {
        transition.AnimatedContent(
            transitionSpec = {
                (fadeIn(animationSpec = tween(800, easing = FastOutSlowInEasing)) + 
                scaleIn(initialScale = 0.92f, animationSpec = tween(800))) togetherWith
                (fadeOut(animationSpec = tween(400)) + 
                scaleOut(targetScale = 1.08f, animationSpec = tween(400)))
            }
        ) { index ->
            val item = featuredItems[index]
            ModernContentCard(
                content = item,
                onClick = { onItemClick(item) },
                isTV = isTV
            )
        }
        
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            featuredItems.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .size(if (index == currentIndex) 60.dp else 16.dp, 3.dp)
                        .clip(RoundedCornerShape(1.5.dp))
                        .background(
                            brush = if (index == currentIndex) {
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            } else {
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.2f),
                                        Color.White.copy(alpha = 0.2f)
                                    )
                                )
                            }
                        )
                        .clickable { currentIndex = index }
                )
            }
        }
    }
}

@Composable
private fun FeaturedContentCard(
    content: FeaturedContent,
    onClick: () -> Unit,
    isTV: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isFocused || isHovered) 1.02f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
        label = "scale"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .scale(scale)
            .focusable(interactionSource = interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        AsyncImage(
            model = content.imageUrl,
            contentDescription = content.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.8f)
                                ),
                                startY = size.height * 0.3f
                            )
                        )
                    }
                }
        )
        
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(if (isTV) 48.dp else 24.dp)
                .fillMaxWidth(0.7f)
        ) {
            Text(
                text = content.title,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = if (isTV) 48.sp else 28.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = content.description,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = if (isTV) 18.sp else 14.sp
                ),
                color = Color.White.copy(alpha = 0.9f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(
                        horizontal = if (isTV) 32.dp else 24.dp,
                        vertical = if (isTV) 16.dp else 12.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        modifier = Modifier.size(if (isTV) 28.dp else 20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Assistir",
                        fontSize = if (isTV) 18.sp else 16.sp
                    )
                }
                
                content.rating?.let { rating ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(if (isTV) 24.dp else 18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = rating,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isTV) 18.sp else 14.sp
                        )
                    }
                }
                
                content.year?.let { year ->
                    Text(
                        text = year,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = if (isTV) 16.sp else 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DarkContentCard(
    content: FeaturedContent,
    onClick: () -> Unit,
    shimmerTranslate: Float,
    isTV: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = content.imageUrl,
            contentDescription = content.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = 0.8f }
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.9f)
                        )
                    )
                )
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.1f),
                                    Color.Transparent
                                ),
                                startX = shimmerTranslate,
                                endX = shimmerTranslate + 200f
                            )
                        )
                    }
                }
        )
        
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(if (isTV) 64.dp else 32.dp)
                .fillMaxWidth(0.8f)
        ) {
            Text(
                text = content.title,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = if (isTV) 56.sp else 32.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = content.description,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = if (isTV) 20.sp else 16.sp
                ),
                color = Color.White.copy(alpha = 0.8f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                ElevatedButton(
                    onClick = onClick,
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    elevation = ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 8.dp
                    ),
                    contentPadding = PaddingValues(
                        horizontal = if (isTV) 40.dp else 32.dp,
                        vertical = if (isTV) 20.dp else 16.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        modifier = Modifier.size(if (isTV) 32.dp else 24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Assistir Agora",
                        fontSize = if (isTV) 20.sp else 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    content.rating?.let { rating ->
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { },
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Rating",
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(if (isTV) 20.dp else 16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = rating,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    content.duration?.let { duration ->
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { },
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = duration,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernContentCard(
    content: FeaturedContent,
    onClick: () -> Unit,
    isTV: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "modern")
    
    val parallaxOffset by infiniteTransition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "parallax"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(32.dp))
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = content.imageUrl,
            contentDescription = content.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .scale(1.1f)
                .graphicsLayer {
                    translationX = parallaxOffset
                }
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        ),
                        radius = 800f
                    )
                )
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(100.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        ),
                        startY = 400f
                    )
                )
        )
        
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(if (isTV) 80.dp else 40.dp)
                .fillMaxWidth(0.75f)
        ) {
            Text(
                text = content.title,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = if (isTV) 64.sp else 36.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                ),
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = content.description,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = if (isTV) 22.sp else 16.sp,
                    lineHeight = if (isTV) 32.sp else 24.sp
                ),
                color = Color.White.copy(alpha = 0.95f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                FilledTonalButton(
                    onClick = onClick,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(
                        horizontal = if (isTV) 48.dp else 36.dp,
                        vertical = if (isTV) 24.dp else 18.dp
                    ),
                    modifier = Modifier.graphicsLayer {
                        shadowElevation = 16.dp.toPx()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        modifier = Modifier.size(if (isTV) 36.dp else 28.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "Reproduzir",
                        fontSize = if (isTV) 24.sp else 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                content.year?.let { year ->
                    Surface(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = year,
                            color = Color.White,
                            fontSize = if (isTV) 18.sp else 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            )
                        )
                    }
                }
            }
        }
    }
}