package com.tvstreaming.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.IntOffset
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay

@Composable
fun ProfessionalHomeHeader(
    userName: String = "Usuário",
    onSearchClick: () -> Unit,
    isTV: Boolean = false
) {
    var currentTime by remember { mutableStateOf(LocalTime.now()) }
    
    // Update time every minute
    LaunchedEffect(Unit) {
        while (true) {
            delay(60000)
            currentTime = LocalTime.now()
        }
    }
    
    val greeting = remember(currentTime) {
        when (currentTime.hour) {
            in 0..11 -> "Bom dia"
            in 12..17 -> "Boa tarde"
            else -> "Boa noite"
        }
    }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = if (isTV) 48.dp else 16.dp,
                vertical = if (isTV) 24.dp else 16.dp
            ),
        shape = RoundedCornerShape(20.dp),
        color = Color.Black.copy(alpha = 0.6f),
        shadowElevation = 8.dp
    ) {
        Box {
            // Remove gradient overlay for cleaner dark theme
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(if (isTV) 24.dp else 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left section - Greeting
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Greeting with animation
                        Column {
                            Text(
                                text = greeting,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontSize = if (isTV) 28.sp else 22.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                            
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = if (isTV) 18.sp else 14.sp
                                ),
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        
                        // Live time display
                        TimeChip(
                            time = currentTime,
                            isTV = isTV
                        )
                    }
                    
                }
                
                // Right section - Actions
                HeaderIconButton(
                    icon = Icons.Default.Search,
                    contentDescription = "Buscar",
                    onClick = onSearchClick,
                    isTV = isTV
                )
            }
        }
    }
}

@Composable
private fun TimeChip(
    time: LocalTime,
    isTV: Boolean
) {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val timeString = time.format(formatter)
    
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.1f),
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Text(
            text = timeString,
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 6.dp
            ),
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = if (isTV) 16.sp else 14.sp,
                fontWeight = FontWeight.Medium
            ),
            color = Color.White
        )
    }
}


@Composable
private fun HeaderIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    isTV: Boolean,
    badge: Boolean = false
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    Box {
        IconButton(
            onClick = {
                isPressed = true
                onClick()
            },
            modifier = Modifier
                .size(if (isTV) 48.dp else 40.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = Color.White,
                modifier = Modifier.size(if (isTV) 24.dp else 20.dp)
            )
        }
        
        if (badge) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = 8.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error)
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

