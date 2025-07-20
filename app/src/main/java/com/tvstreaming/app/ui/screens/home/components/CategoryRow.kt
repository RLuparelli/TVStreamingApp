package com.tvstreaming.app.ui.screens.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items as tvItems
import com.tvstreaming.app.models.Content
import com.tvstreaming.app.ui.components.ContentCard

@Composable
fun CategoryRow(
    title: String,
    contents: List<Content>,
    onContentClick: (String) -> Unit,
    isTV: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = if (isTV) {
                MaterialTheme.typography.headlineMedium
            } else {
                MaterialTheme.typography.titleLarge
            },
            modifier = Modifier.padding(
                horizontal = if (isTV) 48.dp else 16.dp,
                vertical = 8.dp
            )
        )
        
        if (isTV) {
            TvLazyRow(
                contentPadding = PaddingValues(horizontal = 48.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                tvItems(contents) { content ->
                    ContentCard(
                        content = content,
                        onClick = { onContentClick(content.id) }
                    )
                }
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(contents) { content ->
                    ContentCard(
                        content = content,
                        onClick = { onContentClick(content.id) }
                    )
                }
            }
        }
    }
}