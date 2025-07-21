package com.tvstreaming.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

enum class CategoryCardStyle {
    COLORFUL,  // Original CategoryCard style
    DARK,      // DarkCategoryCard style
    MODERN     // ModernCategoryCard style
}

data class CategoryItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val gradientColors: List<Color>,
    val description: String = ""
)

@Composable
fun CategoryCard(
    category: CategoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: CategoryCardStyle = CategoryCardStyle.COLORFUL,
    isTV: Boolean = false
) {
    when (style) {
        CategoryCardStyle.COLORFUL -> ColorfulCategoryCard(
            category = category,
            onClick = onClick,
            modifier = modifier,
            isTV = isTV
        )
        CategoryCardStyle.DARK -> DarkStyleCategoryCard(
            category = category,
            onClick = onClick,
            modifier = modifier,
            isTV = isTV
        )
        CategoryCardStyle.MODERN -> ModernStyleCategoryCard(
            category = category,
            onClick = onClick,
            modifier = modifier,
            isTV = isTV
        )
    }
}

@Composable
private fun ColorfulCategoryCard(
    category: CategoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isTV: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.95f
            isFocused || isHovered -> 1.05f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = 0.4f,
            stiffness = 300f
        ),
        label = "scale"
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "borderColor"
    )
    
    val shadowElevation by animateFloatAsState(
        targetValue = if (isFocused || isHovered) 16f else 8f,
        label = "shadow"
    )
    
    Card(
        modifier = modifier
            .scale(scale)
            .shadow(shadowElevation.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isFocused) 3.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .focusable(interactionSource = interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = category.gradientColors
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (isTV) 24.dp else 20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.title,
                    tint = Color.White,
                    modifier = Modifier.size(if (isTV) 48.dp else 40.dp)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = category.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = if (isTV) 20.sp else 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
                
                if (category.description.isNotEmpty() && (isFocused || isHovered)) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = category.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DarkStyleCategoryCard(
    category: CategoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isTV: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val primaryColor = category.gradientColors.firstOrNull() ?: MaterialTheme.colorScheme.primary
    
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.96f
            isFocused || isHovered -> 1.04f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    val animatedColor by animateColorAsState(
        targetValue = if (isFocused || isHovered) 
            primaryColor 
        else 
            primaryColor.copy(alpha = 0.8f),
        animationSpec = tween(300),
        label = "color"
    )
    
    Surface(
        modifier = modifier
            .scale(scale)
            .focusable(interactionSource = interactionSource),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Color.Black.copy(alpha = 0.5f),
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            animatedColor.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.9f)
                        )
                    )
                )
        ) {
            if (isFocused || isHovered) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    animatedColor.copy(alpha = 0.1f)
                                )
                            )
                        )
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (isTV) 24.dp else 20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    if (isFocused || isHovered) {
                        Icon(
                            imageVector = category.icon,
                            contentDescription = null,
                            tint = animatedColor,
                            modifier = Modifier
                                .size(if (isTV) 56.dp else 48.dp)
                                .scale(1.2f)
                                .graphicsLayer { alpha = 0.3f }
                        )
                    }
                    
                    Icon(
                        imageVector = category.icon,
                        contentDescription = category.title,
                        tint = Color.White,
                        modifier = Modifier.size(if (isTV) 48.dp else 40.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = category.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = if (isTV) 20.sp else 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
            }
            
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(
                        color = if (isFocused || isHovered) 
                            animatedColor 
                        else 
                            Color.Transparent
                    )
            )
        }
    }
}

@Composable
private fun ModernStyleCategoryCard(
    category: CategoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isTV: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val infiniteTransition = rememberInfiniteTransition(label = "card")
    
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.94f
            isFocused || isHovered -> 1.06f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    val rotationZ by animateFloatAsState(
        targetValue = when {
            isFocused || isHovered -> 2f
            else -> 0f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        label = "rotation"
    )
    
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient"
    )
    
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )
    
    Box(
        modifier = modifier
            .scale(scale)
            .graphicsLayer {
                this.rotationZ = rotationZ
                shadowElevation = if (isFocused || isHovered) 20.dp.toPx() else 8.dp.toPx()
            }
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .focusable(interactionSource = interactionSource)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isFocused || isHovered) 12.dp else 6.dp
            ),
            border = if (isFocused) BorderStroke(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary
            ) else null
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawAnimatedGradient(
                        category.gradientColors,
                        gradientOffset,
                        isFocused || isHovered
                    )
                    
                    if (isFocused || isHovered) {
                        drawShimmer(shimmerOffset)
                    }
                }
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(if (isTV) 28.dp else 24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box {
                        if (isFocused || isHovered) {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(if (isTV) 56.dp else 48.dp)
                                    .blur(20.dp)
                                    .graphicsLayer { alpha = 0.5f }
                            )
                        }
                        
                        Icon(
                            imageVector = category.icon,
                            contentDescription = category.title,
                            tint = Color.White,
                            modifier = Modifier.size(if (isTV) 56.dp else 48.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = category.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = if (isTV) 22.sp else 18.sp,
                            fontWeight = FontWeight.Bold,
                            shadow = if (isFocused || isHovered) Shadow(
                                color = Color.Black.copy(alpha = 0.3f),
                                offset = Offset(0f, 4f),
                                blurRadius = 8f
                            ) else null
                        ),
                        color = Color.White
                    )
                    
                    AnimatedVisibility(
                        visible = (isFocused || isHovered) && category.description.isNotEmpty(),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Text(
                            text = category.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawAnimatedGradient(
    colors: List<Color>,
    offset: Float,
    isActive: Boolean
) {
    val gradientColors = if (isActive) {
        colors.map { it.copy(alpha = it.alpha * 1.2f) }
    } else {
        colors
    }
    
    drawRect(
        brush = Brush.linearGradient(
            colors = gradientColors,
            start = Offset(0f, size.height * offset),
            end = Offset(size.width, size.height * (1 - offset))
        )
    )
    
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.2f * offset),
                Color.Transparent
            ),
            center = Offset(
                size.width * (0.5f + sin(offset * PI).toFloat() * 0.3f),
                size.height * (0.5f + cos(offset * PI).toFloat() * 0.3f)
            ),
            radius = size.minDimension * 0.6f
        )
    )
}

private fun DrawScope.drawShimmer(offset: Float) {
    val shimmerWidth = size.width * 0.3f
    val x = size.width * offset
    
    drawRect(
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = 0.3f),
                Color.Transparent
            ),
            startX = x - shimmerWidth / 2,
            endX = x + shimmerWidth / 2
        )
    )
}

@Composable
fun getDefaultCategories(): List<CategoryItem> {
    return listOf(
        CategoryItem(
            id = "live_tv",
            title = "Canais de TV",
            icon = Icons.Default.LiveTv,
            gradientColors = listOf(
                Color(0xFF6B46C1),
                Color(0xFF9333EA)
            ),
            description = "Assista TV ao vivo"
        ),
        CategoryItem(
            id = "movies",
            title = "Filmes",
            icon = Icons.Default.Movie,
            gradientColors = listOf(
                Color(0xFFDC2626),
                Color(0xFFEF4444)
            ),
            description = "Últimos lançamentos"
        ),
        CategoryItem(
            id = "series",
            title = "Séries",
            icon = Icons.Default.Tv,
            gradientColors = listOf(
                Color(0xFF0891B2),
                Color(0xFF06B6D4)
            ),
            description = "Suas séries favoritas"
        ),
        CategoryItem(
            id = "kids",
            title = "Desenhos",
            icon = Icons.Default.ChildCare,
            gradientColors = listOf(
                Color(0xFFF59E0B),
                Color(0xFFFBBF24)
            ),
            description = "Conteúdo infantil"
        )
    )
}