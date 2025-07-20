package com.tvstreaming.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
            // Background pattern
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