package com.tvstreaming.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

@Composable
fun HomeHeader(
    userName: String = "Usuário",
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onProfileClick: () -> Unit,
    isTV: Boolean = false
) {
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
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
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
        // Greeting Section
        Column {
            Text(
                text = greeting,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = if (isTV) 20.sp else 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = if (isTV) 32.sp else 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        // Action Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(if (isTV) 16.dp else 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedIconButton(
                onClick = onSearchClick,
                icon = Icons.Default.Search,
                contentDescription = "Buscar",
                isTV = isTV
            )
            
            AnimatedIconButton(
                onClick = onSettingsClick,
                icon = Icons.Default.Settings,
                contentDescription = "Configurações",
                isTV = isTV
            )
            
            // Profile Avatar
            Surface(
                onClick = onProfileClick,
                modifier = Modifier.size(if (isTV) 48.dp else 40.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Perfil",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(if (isTV) 24.dp else 20.dp)
                    )
                }
            }
        }
        }
    }
}

@Composable
private fun AnimatedIconButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    isTV: Boolean
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = 0.4f,
            stiffness = 300f
        ),
        label = "iconScale"
    )
    
    IconButton(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = Modifier
            .size(if (isTV) 48.dp else 40.dp)
            .scale(scale)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(if (isTV) 28.dp else 24.dp)
        )
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}