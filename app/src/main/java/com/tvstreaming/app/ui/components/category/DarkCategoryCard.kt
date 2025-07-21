package com.tvstreaming.app.ui.components.category

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * CategoryCard com estilo escuro e minimalista
 * Componente focado em uma única responsabilidade
 */
@Composable
internal fun DarkCategoryCard(
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
            // Overlay animado ao focar/hover
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
            
            // Conteúdo
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
                    // Ícone de fundo com blur
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
                    
                    // Ícone principal
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
            
            // Indicador inferior
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