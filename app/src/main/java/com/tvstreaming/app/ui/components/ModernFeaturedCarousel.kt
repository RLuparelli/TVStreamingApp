package com.tvstreaming.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ModernFeaturedCarousel(
    featuredItems: List<FeaturedContent>,
    onItemClick: (FeaturedContent) -> Unit,
    onInfoClick: (FeaturedContent) -> Unit = {},
    onAddToListClick: (FeaturedContent) -> Unit = {},
    modifier: Modifier = Modifier,
    isTV: Boolean = false
) {
    if (featuredItems.isEmpty()) return
    
    var currentIndex by remember { mutableStateOf(0) }
    var isUserInteracting by remember { mutableStateOf(false) }
    var dragOffset by remember { mutableStateOf(0f) }
    
    val transition = updateTransition(targetState = currentIndex, label = "carousel")
    
    // Auto-scroll with pause on interaction
    LaunchedEffect(featuredItems, isUserInteracting) {
        if (!isUserInteracting) {
            while (true) {
                delay(6000) // Change slide every 6 seconds
                currentIndex = (currentIndex + 1) % featuredItems.size
            }
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(if (isTV) 450.dp else 280.dp)
            .clip(RoundedCornerShape(24.dp))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        isUserInteracting = true
                        dragOffset = 0f
                    },
                    onDragEnd = {
                        if (abs(dragOffset) > size.width * 0.2f) {
                            currentIndex = if (dragOffset > 0) {
                                (currentIndex - 1 + featuredItems.size) % featuredItems.size
                            } else {
                                (currentIndex + 1) % featuredItems.size
                            }
                        }
                        dragOffset = 0f
                        isUserInteracting = false
                    },
                    onDrag = { _, dragAmount ->
                        dragOffset += dragAmount.x
                    }
                )
            }
    ) {
        // Modern gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1a1a2e),
                            Color(0xFF0f0f1e)
                        ),
                        radius = 800f
                    )
                )
        )
        
        // Carousel items with 3D effect
        featuredItems.forEachIndexed { index, item ->
            val offsetFromCurrent = (index - currentIndex + featuredItems.size) % featuredItems.size
            val adjustedOffset = if (offsetFromCurrent > featuredItems.size / 2) {
                offsetFromCurrent - featuredItems.size
            } else {
                offsetFromCurrent
            }
            
            CarouselItem(
                item = item,
                offset = adjustedOffset.toFloat() + dragOffset / 300f,
                isActive = index == currentIndex,
                onItemClick = { onItemClick(item) },
                onInfoClick = { onInfoClick(item) },
                onAddToListClick = { onAddToListClick(item) },
                isTV = isTV
            )
        }
        
        // Modern page indicators
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = if (isTV) 40.dp else 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            featuredItems.indices.forEach { index ->
                ModernPageIndicator(
                    isActive = currentIndex == index,
                    onClick = { 
                        currentIndex = index
                        isUserInteracting = true
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CarouselItem(
    item: FeaturedContent,
    offset: Float,
    isActive: Boolean,
    onItemClick: () -> Unit,
    onInfoClick: () -> Unit,
    onAddToListClick: () -> Unit,
    isTV: Boolean
) {
    val scale = 1f - (abs(offset) * 0.15f).coerceAtMost(0.3f)
    val alpha = 1f - (abs(offset) * 0.3f).coerceAtMost(0.7f)
    val rotationY = offset * 25f
    val translationX = offset * 350f
    val translationZ = -abs(offset) * 100f
    
    val animatedScale by animateFloatAsState(
        targetValue = if (isActive) scale else scale * 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    Card(
        onClick = onItemClick,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp, vertical = 20.dp)
            .graphicsLayer {
                this.scaleX = animatedScale
                this.scaleY = animatedScale
                this.alpha = alpha
                this.rotationY = rotationY
                this.translationX = translationX
                this.cameraDistance = 12f * density
                this.transformOrigin = TransformOrigin(0.5f, 0.5f)
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background image with Ken Burns effect
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithCache {
                        onDrawWithContent {
                            drawContent()
                            // Sophisticated gradient overlay
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.3f),
                                        Color.Black.copy(alpha = 0.8f)
                                    ),
                                    startY = size.height * 0.3f,
                                    endY = size.height
                                )
                            )
                            
                            // Side gradient for depth
                            drawRect(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.3f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.3f)
                                    )
                                )
                            )
                        }
                    }
            )
            
            // Animated particles for active item
            if (isActive) {
                AnimatedParticles(
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Content with glass morphism
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (isTV) 40.dp else 24.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                AnimatedVisibility(
                    visible = isActive,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { -it } + fadeOut()
                ) {
                    GlassMorphicSurface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = Color.Black.copy(alpha = 0.3f),
                        borderColor = Color.White.copy(alpha = 0.2f)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            // Title
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontSize = if (isTV) 36.sp else 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    shadow = Shadow(
                                        color = Color.Black.copy(alpha = 0.5f),
                                        offset = Offset(0f, 4f),
                                        blurRadius = 8f
                                    )
                                ),
                                color = Color.White
                            )
                            
                            // Metadata row
                            Row(
                                modifier = Modifier.padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                item.rating?.let {
                                    RatingChip(rating = it)
                                }
                                item.year?.let {
                                    MetadataChip(text = it, icon = null)
                                }
                                item.duration?.let {
                                    MetadataChip(text = it, icon = null)
                                }
                            }
                            
                            // Description
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = 0.9f),
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                            
                            // Action buttons
                            Row(
                                modifier = Modifier.padding(top = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Play button
                                Button(
                                    onClick = onItemClick,
                                    modifier = Modifier.height(if (isTV) 56.dp else 48.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(12.dp)
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
                                
                                // Secondary actions
                                OutlinedIconButton(
                                    onClick = onAddToListClick,
                                    modifier = Modifier.size(if (isTV) 56.dp else 48.dp),
                                    border = BorderStroke(
                                        width = 2.dp,
                                        color = Color.White.copy(alpha = 0.5f)
                                    ),
                                    colors = IconButtonDefaults.outlinedIconButtonColors(
                                        contentColor = Color.White
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Adicionar à lista"
                                    )
                                }
                                
                                OutlinedIconButton(
                                    onClick = onInfoClick,
                                    modifier = Modifier.size(if (isTV) 56.dp else 48.dp),
                                    border = BorderStroke(
                                        width = 2.dp,
                                        color = Color.White.copy(alpha = 0.5f)
                                    ),
                                    colors = IconButtonDefaults.outlinedIconButtonColors(
                                        contentColor = Color.White
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Mais informações"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RatingChip(rating: String) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.star_on),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = rating,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MetadataChip(
    text: String,
    icon: ImageVector?
) {
    Surface(
        color = Color.White.copy(alpha = 0.2f),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun ModernPageIndicator(
    isActive: Boolean,
    onClick: () -> Unit
) {
    val animatedSize by animateDpAsState(
        targetValue = if (isActive) 32.dp else 8.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.5f,
        animationSpec = tween(300)
    )
    
    Surface(
        onClick = onClick,
        modifier = Modifier
            .height(8.dp)
            .width(animatedSize),
        shape = CircleShape,
        color = Color.White.copy(alpha = animatedAlpha),
        border = if (!isActive) BorderStroke(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.3f)
        ) else null
    ) {}
}

@Composable
private fun AnimatedParticles(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val particleOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleOffset"
    )
    
    Canvas(modifier = modifier) {
        repeat(15) { i ->
            val x = (i * 137 + particleOffset * size.width) % size.width
            val y = sin(x / 100 + particleOffset * 2 * PI) * 50 + size.height / 2
            val alpha = (sin(i + particleOffset * 2 * PI) + 1) / 2 * 0.3f
            
            drawCircle(
                color = Color.White.copy(alpha = alpha.toFloat()),
                radius = 2.dp.toPx(),
                center = Offset(x.toFloat(), y.toFloat())
            )
        }
    }
}