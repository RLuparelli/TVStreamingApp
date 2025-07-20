package com.tvstreaming.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.dp

@Composable
fun GlassMorphicCard(
    modifier: Modifier = Modifier,
    blurRadius: Float = 25f,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable BoxScope.() -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    
    val animatedBlur by animateFloatAsState(
        targetValue = if (isFocused) blurRadius * 1.2f else blurRadius,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "blur"
    )
    
    val borderAlpha by animateFloatAsState(
        targetValue = if (isFocused) 0.3f else 0.15f,
        animationSpec = tween(300),
        label = "borderAlpha"
    )
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
    ) {
        // Blur background layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .blur(animatedBlur.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.1f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    )
                )
        )
        
        // Glass surface
        Surface(
            modifier = Modifier
                .matchParentSize()
                .onFocusChanged { isFocused = it.isFocused },
            color = Color.White.copy(alpha = 0.1f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(
                width = 1.dp,
                color = Color.White.copy(alpha = borderAlpha)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(100f, 100f)
                        )
                    )
                    .padding(contentPadding),
                content = content
            )
        }
    }
}

@Composable
fun GlassMorphicSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    blurRadius: Float = 10f,
    backgroundColor: Color = Color.Black.copy(alpha = 0.5f),
    borderColor: Color = Color.White.copy(alpha = 0.3f),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.clip(shape)
    ) {
        // Frosted glass effect background
        Box(
            modifier = Modifier
                .matchParentSize()
                .blur(blurRadius.dp)
                .background(backgroundColor)
        )
        
        // Main content with border
        Surface(
            modifier = Modifier.matchParentSize(),
            color = backgroundColor,
            shape = shape,
            border = BorderStroke(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        borderColor,
                        borderColor.copy(alpha = 0.1f)
                    )
                )
            )
        ) {
            // Noise texture overlay for realism
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.05f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.05f)
                            )
                        )
                    )
            ) {
                content()
            }
        }
    }
}