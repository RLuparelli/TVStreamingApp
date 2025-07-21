package com.tvstreaming.app.ui.components.category

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.Color

/**
 * Valores padrão e funções auxiliares para categorias
 * Centraliza definições de categorias seguindo DRY
 */
object CategoryDefaults {
    
    /**
     * Retorna as categorias padrão do aplicativo
     */
    fun getDefaultCategories(): List<CategoryItem> = listOf(
        CategoryItem(
            id = "live_tv",
            title = "TV ao Vivo",
            icon = Icons.Default.LiveTv,
            gradientColors = listOf(Color(0xFF4A148C), Color(0xFF7B1FA2)),
            description = "Canais ao vivo"
        ),
        CategoryItem(
            id = "movies",
            title = "Filmes",
            icon = Icons.Default.Movie,
            gradientColors = listOf(Color(0xFFB71C1C), Color(0xFFE53935)),
            description = "Catálogo de filmes"
        ),
        CategoryItem(
            id = "series",
            title = "Séries",
            icon = Icons.Default.Tv,
            gradientColors = listOf(Color(0xFF1A237E), Color(0xFF3949AB)),
            description = "Séries e programas"
        ),
        CategoryItem(
            id = "kids",
            title = "Infantil",
            icon = Icons.Default.ChildCare,
            gradientColors = listOf(Color(0xFF00C853), Color(0xFF69F0AE)),
            description = "Conteúdo infantil"
        )
    )
    
    /**
     * Retorna cores gradiente baseado no ID da categoria
     */
    fun getGradientColors(categoryId: String): List<Color> {
        return when (categoryId) {
            "live_tv" -> listOf(Color(0xFF4A148C), Color(0xFF7B1FA2))
            "movies" -> listOf(Color(0xFFB71C1C), Color(0xFFE53935))
            "series" -> listOf(Color(0xFF1A237E), Color(0xFF3949AB))
            "kids", "animation" -> listOf(Color(0xFF00C853), Color(0xFF69F0AE))
            "sports" -> listOf(Color(0xFFE65100), Color(0xFFFF9800))
            "news" -> listOf(Color(0xFF263238), Color(0xFF546E7A))
            "documentary" -> listOf(Color(0xFF3E2723), Color(0xFF6D4C41))
            else -> listOf(Color(0xFF424242), Color(0xFF757575))
        }
    }
}