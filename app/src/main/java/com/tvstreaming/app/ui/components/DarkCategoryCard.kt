package com.tvstreaming.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class DarkCategoryItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val primaryColor: Color,
    val secondaryColor: Color = Color.Black
)

@Composable
fun DarkCategoryCard(
    category: DarkCategoryItem,
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
            category.primaryColor 
        else 
            category.primaryColor.copy(alpha = 0.8f),
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
            // Border effect on focus
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
                // Icon with glow effect
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
            
            // Bottom accent line
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
fun getDarkCategories(): List<DarkCategoryItem> {
    return listOf(
        DarkCategoryItem(
            id = "live_tv",
            title = "Canais de TV",
            icon = Icons.Default.LiveTv,
            primaryColor = Color(0xFF7C3AED) // Purple
        ),
        DarkCategoryItem(
            id = "movies",
            title = "Filmes",
            icon = Icons.Default.Movie,
            primaryColor = Color(0xFFEF4444) // Red
        ),
        DarkCategoryItem(
            id = "series",
            title = "Séries",
            icon = Icons.Default.Tv,
            primaryColor = Color(0xFF06B6D4) // Cyan
        ),
        DarkCategoryItem(
            id = "kids",
            title = "Desenhos",
            icon = Icons.Default.ChildCare,
            primaryColor = Color(0xFFF59E0B) // Amber
        )
    )
}