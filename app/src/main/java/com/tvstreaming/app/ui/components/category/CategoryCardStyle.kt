package com.tvstreaming.app.ui.components.category

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Enum para diferentes estilos de CategoryCard
 */
enum class CategoryCardStyle {
    COLORFUL,  // Original CategoryCard style
    DARK,      // DarkCategoryCard style
    MODERN     // ModernCategoryCard style
}

/**
 * Modelo de dados para uma categoria
 */
data class CategoryItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val gradientColors: List<Color>,
    val description: String = ""
)