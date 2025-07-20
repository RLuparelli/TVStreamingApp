package com.tvstreaming.app.ui.screens.channels.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tvstreaming.app.models.ChannelCategory

@Composable
fun CategoryFilter(
    categories: List<ChannelCategory>,
    selectedCategory: ChannelCategory?,
    onCategorySelected: (ChannelCategory?) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isLoading && categories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botão "Todos"
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { onCategorySelected(null) },
                    label = {
                        Text(
                            text = "Todos",
                            color = if (selectedCategory == null) Color.White else Color.White.copy(alpha = 0.8f),
                            fontWeight = if (selectedCategory == null) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.White.copy(alpha = 0.1f),
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedCategory == null,
                        borderColor = Color.White.copy(alpha = 0.3f),
                        selectedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
                
                // Categorias
                categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory?.id == category.id,
                        onClick = { onCategorySelected(category) },
                        label = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = category.icon,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = category.name,
                                    color = if (selectedCategory?.id == category.id) Color.White else Color.White.copy(alpha = 0.8f),
                                    fontWeight = if (selectedCategory?.id == category.id) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.White.copy(alpha = 0.1f),
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedCategory?.id == category.id,
                            borderColor = Color.White.copy(alpha = 0.3f),
                            selectedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}