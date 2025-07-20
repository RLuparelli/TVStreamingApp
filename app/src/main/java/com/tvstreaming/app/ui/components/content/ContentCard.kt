package com.tvstreaming.app.ui.components.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tvstreaming.app.models.MediaContent
import com.tvstreaming.app.models.MediaType

/**
 * Card genérico para exibir qualquer tipo de conteúdo
 * Seguindo Liskov Substitution Principle - funciona com qualquer MediaContent
 */
@Composable
fun ContentCard(
    content: MediaContent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showRating: Boolean = true,
    showBadge: Boolean = true
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Imagem do poster
            AsyncImage(
                model = content.posterUrl,
                contentDescription = content.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            // Badge de rating
            if (showRating) {
                content.rating?.let { rating ->
                    Surface(
                        color = Color.Black.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 0.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 8.dp
                        ),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp)
                    ) {
                        Text(
                            text = "★ $rating",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
            
            // Badge de tipo de conteúdo
            if (showBadge) {
                ContentTypeBadge(
                    mediaType = content.mediaType,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                )
            }
        }
    }
}

/**
 * Badge para indicar o tipo de conteúdo
 */
@Composable
private fun ContentTypeBadge(
    mediaType: MediaType,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (mediaType) {
        MediaType.MOVIE -> "Filme" to Color(0xFF4CAF50)
        MediaType.SERIES -> "Série" to Color(0xFF2196F3)
        MediaType.ANIMATION -> "Desenho" to Color(0xFFFF9800)
        MediaType.LIVE -> "Ao Vivo" to Color(0xFFF44336)
    }
    
    Surface(
        color = color.copy(alpha = 0.9f),
        shape = RoundedCornerShape(
            topStart = 8.dp,
            topEnd = 0.dp,
            bottomStart = 8.dp,
            bottomEnd = 0.dp
        ),
        modifier = modifier
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}