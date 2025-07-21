package com.tvstreaming.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import java.text.SimpleDateFormat
import java.util.*

enum class HeaderStyle {
    COLORFUL,     // Original HomeHeader style
    MINIMAL,      // MinimalHomeHeader style
    MODERN,       // ModernHomeHeader style
    PROFESSIONAL  // ProfessionalHomeHeader style
}

@Composable
fun HomeHeader(
    userName: String = "Usuário",
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    style: HeaderStyle = HeaderStyle.COLORFUL,
    isTV: Boolean = false
) {
    when (style) {
        HeaderStyle.COLORFUL -> ColorfulHomeHeader(
            userName = userName,
            onSearchClick = onSearchClick,
            onSettingsClick = onSettingsClick,
            onProfileClick = onProfileClick,
            isTV = isTV
        )
        HeaderStyle.MINIMAL -> MinimalStyleHomeHeader(
            userName = userName,
            onSearchClick = onSearchClick,
            onSettingsClick = onSettingsClick,
            isTV = isTV
        )
        HeaderStyle.MODERN -> ModernStyleHomeHeader(
            userName = userName,
            onSearchClick = onSearchClick,
            onProfileClick = onProfileClick,
            isTV = isTV
        )
        HeaderStyle.PROFESSIONAL -> ProfessionalStyleHomeHeader(
            userName = userName,
            onSearchClick = onSearchClick,
            isTV = isTV
        )
    }
}

@Composable
private fun ColorfulHomeHeader(
    userName: String,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onProfileClick: () -> Unit,
    isTV: Boolean
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val isCompact = screenWidthDp < 600
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
                horizontal = when {
                    isTV -> 48.dp
                    isCompact -> 12.dp
                    else -> 16.dp
                },
                vertical = when {
                    isTV -> 24.dp
                    isCompact -> 12.dp
                    else -> 16.dp
                }
            ),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = when {
                            isTV -> 32.dp
                            isCompact -> 16.dp
                            else -> 20.dp
                        },
                        vertical = when {
                            isTV -> 24.dp
                            isCompact -> 12.dp
                            else -> 16.dp
                        }
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontSize = if (isTV) 18.sp else 14.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = when {
                                isTV -> 32.sp
                                isCompact -> 20.sp
                                else -> 24.sp
                            },
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(if (isTV) 16.dp else 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HeaderIconButton(
                        icon = Icons.Default.Search,
                        contentDescription = "Buscar",
                        onClick = onSearchClick,
                        isTV = isTV
                    )
                    
                    HeaderIconButton(
                        icon = Icons.Default.Settings,
                        contentDescription = "Configurações",
                        onClick = onSettingsClick,
                        isTV = isTV
                    )
                    
                    Surface(
                        modifier = Modifier
                            .size(if (isTV) 56.dp else 40.dp)
                            .clip(CircleShape)
                            .clickable { onProfileClick() },
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Perfil",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(if (isTV) 32.dp else 24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MinimalStyleHomeHeader(
    userName: String,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    isTV: Boolean
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val isCompact = screenWidthDp < 600
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = when {
                    isTV -> 48.dp
                    isCompact -> 16.dp
                    else -> 24.dp
                },
                vertical = when {
                    isTV -> 24.dp
                    isCompact -> 12.dp
                    else -> 16.dp
                }
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "TV Streaming",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = when {
                    isTV -> 32.sp
                    isCompact -> 20.sp
                    else -> 24.sp
                },
                fontWeight = FontWeight.Light,
                letterSpacing = if (isCompact) 0.5.sp else 1.sp
            ),
            color = Color.White
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(if (isTV) 20.dp else 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onSearchClick,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White.copy(alpha = 0.8f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier.size(if (isTV) 28.dp else 20.dp)
                )
                if (isTV) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Buscar")
                }
            }
            
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.size(if (isTV) 48.dp else 40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(if (isTV) 28.dp else 20.dp)
                )
            }
        }
    }
}

@Composable
private fun ModernStyleHomeHeader(
    userName: String,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    isTV: Boolean
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val isCompact = screenWidthDp < 600
    val infiniteTransition = rememberInfiniteTransition(label = "modern_header")
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = when {
                    isTV -> 48.dp
                    isCompact -> 16.dp
                    else -> 20.dp
                },
                vertical = when {
                    isTV -> 32.dp
                    isCompact -> 16.dp
                    else -> 20.dp
                }
            ),
        shape = RoundedCornerShape(24.dp),
        color = Color.Black.copy(alpha = 0.4f),
        shadowElevation = 12.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f * glowAlpha),
                            Color.Transparent,
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f * glowAlpha)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = when {
                            isTV -> 40.dp
                            isCompact -> 20.dp
                            else -> 24.dp
                        },
                        vertical = when {
                            isTV -> 28.dp
                            isCompact -> 16.dp
                            else -> 20.dp
                        }
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val currentTime = remember { 
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                    }
                    
                    Text(
                        text = currentTime,
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontSize = when {
                                isTV -> 36.sp
                                isCompact -> 24.sp
                                else -> 28.sp
                            },
                            fontWeight = FontWeight.Thin,
                            letterSpacing = if (isCompact) 1.sp else 2.sp
                        ),
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Bem-vindo, $userName",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = if (isTV) 18.sp else 14.sp
                        ),
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(if (isTV) 24.dp else 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ModernIconButton(
                        icon = Icons.Default.Search,
                        onClick = onSearchClick,
                        isTV = isTV
                    )
                    
                    ModernIconButton(
                        icon = Icons.Default.Notifications,
                        onClick = { },
                        badge = true,
                        isTV = isTV
                    )
                    
                    Surface(
                        modifier = Modifier
                            .size(if (isTV) 64.dp else 48.dp)
                            .clip(CircleShape)
                            .clickable { onProfileClick() },
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color.White,
                                modifier = Modifier.size(if (isTV) 36.dp else 28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfessionalStyleHomeHeader(
    userName: String,
    onSearchClick: () -> Unit,
    isTV: Boolean
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val screenWidthDp = configuration.screenWidthDp
    val screenHeightDp = configuration.screenHeightDp
    val isCompact = screenWidthDp < 600
    val isSmallHeight = screenHeightDp < 600
    
    val currentDate = remember {
        SimpleDateFormat("EEEE, d 'de' MMMM", Locale("pt", "BR")).format(Date())
    }
    
    // Ajuste dinâmico de paddings baseado no tamanho da tela
    val horizontalPadding = when {
        isTV -> 64.dp
        isLandscape && !isCompact -> 48.dp
        isLandscape && isCompact -> 32.dp
        isCompact -> 16.dp
        else -> 24.dp
    }
    
    val verticalPadding = when {
        isTV -> 40.dp
        isLandscape || isSmallHeight -> 12.dp
        isCompact -> 16.dp
        else -> 24.dp
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = horizontalPadding,
                vertical = verticalPadding
            )
    ) {
        // Layout adaptativo para diferentes tamanhos de tela
        if (!isLandscape && (isCompact || screenWidthDp < 800)) {
            // Layout vertical para retrato
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                // Logo e data em linha horizontal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "TV STREAMING",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = if (isCompact) 11.sp else 12.sp,
                                letterSpacing = if (isCompact) 1.5.sp else 2.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = currentDate,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = if (isCompact) 18.sp else 22.sp,
                                fontWeight = FontWeight.Light
                            ),
                            color = Color.White,
                            maxLines = if (isCompact) 1 else 2
                        )
                    }
                    
                    // Botão de busca compacto para mobile
                    IconButton(
                        onClick = onSearchClick,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        } else {
            // Layout horizontal padrão para landscape e telas maiores
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "TV STREAMING",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = when {
                                isTV -> 16.sp
                                isLandscape -> 12.sp
                                else -> 11.sp
                            },
                            letterSpacing = if (isTV) 3.sp else 2.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(if (isCompact) 4.dp else 8.dp))
                    
                    Text(
                        text = currentDate,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = when {
                                isTV -> 48.sp
                                isLandscape && !isCompact -> 32.sp
                                isLandscape -> 24.sp
                                else -> 28.sp
                            },
                            fontWeight = FontWeight.ExtraLight
                        ),
                        color = Color.White,
                        maxLines = if (isCompact) 1 else 2
                    )
                }
                
                // Botão de busca adaptativo
                Surface(
                    onClick = onSearchClick,
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.1f),
                    modifier = Modifier
                        .padding(top = if (isTV) 8.dp else 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(
                                horizontal = when {
                                    isTV -> 32.dp
                                    isLandscape -> 20.dp
                                    else -> 16.dp
                                },
                                vertical = when {
                                    isTV -> 16.dp
                                    isLandscape -> 10.dp
                                    else -> 8.dp
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(if (isTV) 24.dp else 18.dp)
                        )
                        
                        // Mostrar texto completo apenas em telas maiores
                        if (isTV || (isLandscape && !isCompact)) {
                            Text(
                                text = "Buscar filmes, séries...",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = if (isTV) 18.sp else 14.sp
                                ),
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(when {
            isTV -> 32.dp
            isLandscape || isSmallHeight -> 8.dp
            isCompact -> 12.dp
            else -> 20.dp
        }))
        
        // Linha divisória
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

@Composable
private fun HeaderIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    isTV: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isFocused || isHovered) 1.1f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 300f),
        label = "scale"
    )
    
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(if (isTV) 56.dp else 40.dp)
            .scale(scale)
            .focusable(interactionSource = interactionSource),
        interactionSource = interactionSource
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(if (isTV) 28.dp else 24.dp)
        )
    }
}

@Composable
private fun ModernIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    badge: Boolean = false,
    isTV: Boolean
) {
    Box {
        Surface(
            onClick = onClick,
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.1f),
            modifier = Modifier
                .size(if (isTV) 56.dp else 44.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(if (isTV) 28.dp else 24.dp)
                )
            }
        }
        
        if (badge) {
            Box(
                modifier = Modifier
                    .size(if (isTV) 12.dp else 8.dp)
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(Color.Red)
            )
        }
    }
}