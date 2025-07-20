package com.tvstreaming.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Composable
fun ModernHomeHeader(
    userName: String = "Usuário",
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit = {},
    isTV: Boolean = false
) {
    val currentTime = remember { mutableStateOf(LocalDateTime.now()) }
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 0..11 -> "Bom dia"
            in 12..17 -> "Boa tarde"
            else -> "Boa noite"
        }
    }
    
    // Update time every minute
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(60000) // 1 minute
            currentTime.value = LocalDateTime.now()
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = if (isTV) 48.dp else 16.dp,
                vertical = if (isTV) 24.dp else 16.dp
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isTV) 24.dp else 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left section - Greeting and time
            Column {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = if (isTV) 32.sp else 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    // Animated time display
                    AnimatedTimeDisplay(
                        time = currentTime.value,
                        isTV = isTV
                    )
                }
                
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = if (isTV) 20.sp else 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            // Right section - Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(if (isTV) 20.dp else 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ModernIconButton(
                    onClick = onSearchClick,
                    icon = Icons.Default.Search,
                    contentDescription = "Buscar",
                    isTV = isTV
                )
                
                ModernIconButton(
                    onClick = onNotificationsClick,
                    icon = Icons.Default.Notifications,
                    contentDescription = "Notificações",
                    isTV = isTV,
                    showBadge = true
                )
                
                ModernIconButton(
                    onClick = onSettingsClick,
                    icon = Icons.Default.Settings,
                    contentDescription = "Configurações",
                    isTV = isTV
                )
                
                // Profile Avatar with animated border
                ModernProfileAvatar(
                    onClick = onProfileClick,
                    userName = userName,
                    isTV = isTV
                )
            }
        }
    }
}

@Composable
private fun AnimatedTimeDisplay(
    time: LocalDateTime,
    isTV: Boolean
) {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val timeString = time.format(formatter)
    
    Surface(
        color = Color.White.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.2f)
        )
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
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

@Composable
private fun ModernIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    isTV: Boolean,
    showBadge: Boolean = false
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    Box {
        Surface(
            onClick = {
                isPressed = true
                onClick()
            },
            modifier = Modifier
                .size(if (isTV) 48.dp else 40.dp)
                .scale(scale),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.1f),
            border = BorderStroke(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.2f)
            )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = Color.White,
                    modifier = Modifier.size(if (isTV) 24.dp else 20.dp)
                )
            }
        }
        
        // Notification badge
        if (showBadge) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.error,
                        shape = CircleShape
                    )
            )
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

@Composable
private fun ModernProfileAvatar(
    onClick: () -> Unit,
    userName: String,
    isTV: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "avatar")
    val animatedRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    Surface(
        onClick = onClick,
        modifier = Modifier.size(if (isTV) 52.dp else 44.dp),
        shape = CircleShape,
        color = Color.Transparent
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Animated gradient border
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationZ = animatedRotation }
            ) {
                val gradientBrush = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFF6B46C1),
                        Color(0xFF0891B2),
                        Color(0xFFDC2626),
                        Color(0xFFF59E0B),
                        Color(0xFF6B46C1)
                    ),
                    center = center
                )
                
                drawCircle(
                    brush = gradientBrush,
                    radius = size.minDimension / 2,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
            
            // Inner avatar
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = userName.firstOrNull()?.uppercase() ?: "U",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = if (isTV) 20.sp else 16.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}