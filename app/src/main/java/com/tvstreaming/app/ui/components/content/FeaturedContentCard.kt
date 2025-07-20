package com.tvstreaming.app.ui.components.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tvstreaming.app.models.MediaContent

/**
 * Card de destaque genérico para qualquer tipo de conteúdo
 * Seguindo Open/Closed Principle - aberto para extensão
 */
@Composable
fun FeaturedContentCard(
    content: MediaContent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showMetadata: Boolean = true,
    gradientStartY: Float = 200f
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Imagem de fundo
            AsyncImage(
                model = content.backdropUrl ?: content.posterUrl,
                contentDescription = content.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            // Gradiente overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.9f)
                            ),
                            startY = gradientStartY
                        )
                    )
            )
            
            // Conteúdo
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp)
            ) {
                Text(
                    text = content.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                
                if (showMetadata) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    content.description?.let { description ->
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.9f),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    ContentMetadataRow(content = content)
                }
            }
        }
    }
}

/**
 * Componente de metadata reutilizável
 */
@Composable
fun ContentMetadataRow(
    content: MediaContent,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Rating
        content.rating?.let { rating ->
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "★ $rating",
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
        
        // Ano
        content.year?.let { year ->
            Text(
                text = year,
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        
        // Duração
        content.duration?.let { duration ->
            Text(
                text = formatDuration(duration),
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

/**
 * Formata duração em minutos para texto legível
 */
private fun formatDuration(minutes: Int): String {
    return if (minutes >= 60) {
        "${minutes / 60}h ${minutes % 60}min"
    } else {
        "${minutes}min"
    }
}