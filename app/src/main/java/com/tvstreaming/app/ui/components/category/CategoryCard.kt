package com.tvstreaming.app.ui.components.category

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Componente principal que delega para o estilo apropriado
 * Seguindo o princípio Single Responsibility
 */
@Composable
fun CategoryCard(
    category: CategoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: CategoryCardStyle = CategoryCardStyle.COLORFUL,
    isTV: Boolean = false
) {
    when (style) {
        CategoryCardStyle.COLORFUL -> ColorfulCategoryCard(
            category = category,
            onClick = onClick,
            modifier = modifier,
            isTV = isTV
        )
        CategoryCardStyle.DARK -> DarkCategoryCard(
            category = category,
            onClick = onClick,
            modifier = modifier,
            isTV = isTV
        )
        CategoryCardStyle.MODERN -> ModernCategoryCard(
            category = category,
            onClick = onClick,
            modifier = modifier,
            isTV = isTV
        )
    }
}