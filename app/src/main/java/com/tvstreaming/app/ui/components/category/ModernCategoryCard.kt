package com.tvstreaming.app.ui.components.category

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

/**
 * CategoryCard com estilo moderno e animações complexas
 * Componente focado em uma única responsabilidade
 */
@Composable
internal fun ModernCategoryCard(
    category: CategoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isTV: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val infiniteTransition = rememberInfiniteTransition(label = "modern_card")
    
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
                // Canvas para desenhar gradientes animados
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
                
                // Conteúdo
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(if (isTV) 28.dp else 24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box {
                        // Ícone com blur de fundo
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
                        
                        // Ícone principal
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
                    
                    // Descrição animada
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

/**
 * Função para desenhar gradiente animado
 */
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
    
    // Círculo animado
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

/**
 * Função para desenhar efeito shimmer
 */
private fun DrawScope.drawShimmer(offset: Float) {
    val shimmerWidth = size.width * 0.3f
    val shimmerX = size.width * offset
    
    drawRect(
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = 0.1f),
                Color.White.copy(alpha = 0.2f),
                Color.White.copy(alpha = 0.1f),
                Color.Transparent
            ),
            startX = shimmerX - shimmerWidth,
            endX = shimmerX + shimmerWidth
        )
    )
}