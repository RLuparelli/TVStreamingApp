package com.tvstreaming.app.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.delay

@Composable
fun MinimalHomeHeader(
    userName: String = "Usuário",
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    isTV: Boolean = false
) {
    val currentTime = remember { mutableStateOf(Date()) }
    
    LaunchedEffect(Unit) {
        while (true) {
            currentTime.value = Date()
            delay(60000) // Update every minute
        }
    }
    
    val greeting = remember(currentTime.value) {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 0..11 -> "Bom dia"
            in 12..17 -> "Boa tarde"
            else -> "Boa noite"
        }
    }
    
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateFormat = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("pt", "BR"))
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = if (isTV) 48.dp else 16.dp,
                vertical = if (isTV) 24.dp else 16.dp
            )
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.03f),
                            Color.Transparent
                        )
                    )
                )
                .padding(if (isTV) 28.dp else 20.dp)
        ) {
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Left: Greeting and Date
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "$greeting,",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = if (isTV) 24.sp else 18.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = if (isTV) 32.sp else 26.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    
                    // Date and Time
                    Row(
                        modifier = Modifier.padding(top = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarToday,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = dateFormat.format(currentTime.value).replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = timeFormat.format(currentTime.value),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                
                // Right: Actions
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MinimalActionButton(
                        icon = Icons.Default.Search,
                        contentDescription = "Buscar",
                        onClick = onSearchClick,
                        isTV = isTV
                    )
                    
                    MinimalProfileButton(
                        userName = userName,
                        onClick = onProfileClick,
                        isTV = isTV
                    )
                }
            }
            
            // Optional: Quick Stats or Status
            if (isTV) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    QuickStat(
                        icon = Icons.Outlined.PlayCircle,
                        label = "Continue assistindo",
                        value = "3 títulos"
                    )
                    QuickStat(
                        icon = Icons.Outlined.Favorite,
                        label = "Minha lista",
                        value = "12 títulos"
                    )
                    QuickStat(
                        icon = Icons.Outlined.TrendingUp,
                        label = "Em alta",
                        value = "5 novos"
                    )
                }
            }
        }
    }
}

@Composable
private fun MinimalActionButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    isTV: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    FilledTonalIconButton(
        onClick = onClick,
        modifier = Modifier.size(if (isTV) 48.dp else 40.dp),
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        interactionSource = interactionSource
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(if (isTV) 24.dp else 20.dp)
        )
    }
}

@Composable
private fun MinimalProfileButton(
    userName: String,
    onClick: () -> Unit,
    isTV: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    Surface(
        onClick = onClick,
        modifier = Modifier.size(if (isTV) 48.dp else 40.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.tertiaryContainer,
        interactionSource = interactionSource
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (userName.length >= 2) {
                Text(
                    text = userName.take(2).uppercase(),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = if (isTV) 16.sp else 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(if (isTV) 24.dp else 20.dp)
                )
            }
        }
    }
}

@Composable
private fun QuickStat(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}