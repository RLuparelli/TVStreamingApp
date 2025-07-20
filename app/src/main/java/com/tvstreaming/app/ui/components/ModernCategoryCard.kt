package com.tvstreaming.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*
import kotlin.random.Random

@Composable
fun ModernCategoryCard(
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
    
    // Animações suaves
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
    
    // Animação de gradiente pulsante
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient"
    )
    
    // Brilho animado
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
        // Glass morphism container
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
                // Animated gradient background
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawAnimatedGradient(
                        category.gradientColors,
                        gradientOffset,
                        isFocused || isHovered
                    )
                    
                    // Shimmer effect
                    if (isFocused || isHovered) {
                        drawShimmer(shimmerOffset)
                    }
                }
                
                // Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(if (isTV) 28.dp else 24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icon with glow effect
                    Box {
                        // Glow
                        if (isFocused || isHovered) {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(if (isTV) 56.dp else 48.dp)
                                    .blur(20.dp)
                                    .alpha(0.5f)
                            )
                        }
                        
                        // Main icon
                        Icon(
                            imageVector = category.icon,
                            contentDescription = category.title,
                            tint = Color.White,
                            modifier = Modifier.size(if (isTV) 56.dp else 48.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Title with animated shadow
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
                    
                    // Animated description
                    androidx.compose.animation.AnimatedVisibility(
                        visible = (isFocused || isHovered) && category.description.isNotEmpty(),
                        enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.expandVertically(),
                        exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.shrinkVertically()
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
    
    // Radial accent
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
private fun ParticleOverlay(
    modifier: Modifier = Modifier,
    particleCount: Int = 20,
    colors: List<Color>
) {
    val particles = remember {
        List(particleCount) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 3 + 1,
                speed = Random.nextFloat() * 0.02f + 0.01f,
                color = colors.random().copy(alpha = Random.nextFloat() * 0.3f + 0.1f)
            )
        }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleTime"
    )
    
    Canvas(modifier = modifier) {
        particles.forEach { particle ->
            val y = (particle.y - time * particle.speed) % 1f
            val adjustedY = if (y < 0) y + 1f else y
            
            drawCircle(
                color = particle.color,
                radius = particle.size,
                center = Offset(
                    particle.x * size.width,
                    adjustedY * size.height
                )
            )
        }
    }
}

private data class Particle(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val color: Color
)