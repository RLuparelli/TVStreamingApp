package com.tvstreaming.app.ui.components.content

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tvstreaming.app.models.MediaContent

/**
 * Row genérico para exibir conteúdos por categoria
 * Seguindo Dependency Inversion Principle - depende de abstrações (MediaContent)
 */
@Composable
fun ContentCategoryRow(
    categoryName: String,
    contents: List<MediaContent>,
    onContentClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentWidth: Dp = 150.dp,
    contentHeight: Dp = 225.dp,
    horizontalPadding: Dp = 16.dp,
    itemSpacing: Dp = 12.dp
) {
    Column(modifier = modifier) {
        // Título da categoria
        Text(
            text = categoryName,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = horizontalPadding, vertical = 8.dp)
        )
        
        // Lista horizontal de conteúdos
        LazyRow(
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(itemSpacing)
        ) {
            items(
                items = contents,
                key = { it.id }
            ) { content ->
                ContentCard(
                    content = content,
                    onClick = { onContentClick(content.id) },
                    modifier = Modifier
                        .width(contentWidth)
                        .height(contentHeight)
                )
            }
        }
    }
}