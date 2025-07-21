package com.tvstreaming.app.core.config

import com.tvstreaming.app.models.MediaType

/**
 * Configuração centralizada de categorias
 * Segue DRY centralizando definições de categorias
 */
object CategoryConfig {
    
    /**
     * Mapeamento de categorias por tipo de mídia
     */
    val CATEGORIES_BY_TYPE = mapOf(
        MediaType.MOVIE to listOf(
            CategoryInfo("action", "Ação", "Filmes de ação e aventura"),
            CategoryInfo("comedy", "Comédia", "Filmes para dar risadas"),
            CategoryInfo("drama", "Drama", "Histórias emocionantes"),
            CategoryInfo("horror", "Terror", "Filmes assustadores"),
            CategoryInfo("romance", "Romance", "Histórias de amor"),
            CategoryInfo("scifi", "Ficção Científica", "Filmes futuristas"),
            CategoryInfo("thriller", "Suspense", "Filmes de suspense"),
            CategoryInfo("documentary", "Documentário", "Documentários")
        ),
        
        MediaType.SERIES to listOf(
            CategoryInfo("action", "Ação", "Séries de ação e aventura"),
            CategoryInfo("comedy", "Comédia", "Séries de humor"),
            CategoryInfo("drama", "Drama", "Séries dramáticas"),
            CategoryInfo("reality", "Reality", "Reality shows"),
            CategoryInfo("documentary", "Documentário", "Séries documentais"),
            CategoryInfo("anime", "Anime", "Animações japonesas"),
            CategoryInfo("crime", "Crime", "Séries policiais"),
            CategoryInfo("scifi", "Ficção", "Séries de ficção científica")
        ),
        
        MediaType.ANIMATION to listOf(
            CategoryInfo("kids", "Infantil", "Para crianças pequenas"),
            CategoryInfo("family", "Família", "Para toda a família"),
            CategoryInfo("educational", "Educativo", "Conteúdo educacional"),
            CategoryInfo("adventure", "Aventura", "Desenhos de aventura"),
            CategoryInfo("comedy", "Comédia", "Desenhos engraçados"),
            CategoryInfo("fantasy", "Fantasia", "Mundos mágicos"),
            CategoryInfo("action", "Ação", "Desenhos de ação"),
            CategoryInfo("musical", "Musical", "Desenhos musicais")
        ),
        
        MediaType.LIVE to listOf(
            CategoryInfo("news", "Notícias", "Canais de notícias"),
            CategoryInfo("sports", "Esportes", "Canais esportivos"),
            CategoryInfo("entertainment", "Entretenimento", "Variedades"),
            CategoryInfo("documentary", "Documentários", "Canais educativos"),
            CategoryInfo("kids", "Infantil", "Canais infantis"),
            CategoryInfo("movies", "Filmes", "Canais de filmes"),
            CategoryInfo("music", "Música", "Canais musicais"),
            CategoryInfo("international", "Internacional", "Canais internacionais")
        )
    )
    
    /**
     * Categorias principais do app
     */
    val MAIN_CATEGORIES = listOf(
        MainCategoryInfo(
            id = "live_tv",
            title = "TV ao Vivo",
            description = "Canais ao vivo",
            mediaType = MediaType.LIVE
        ),
        MainCategoryInfo(
            id = "movies",
            title = "Filmes",
            description = "Catálogo de filmes",
            mediaType = MediaType.MOVIE
        ),
        MainCategoryInfo(
            id = "series",
            title = "Séries",
            description = "Séries e programas",
            mediaType = MediaType.SERIES
        ),
        MainCategoryInfo(
            id = "kids",
            title = "Infantil",
            description = "Conteúdo infantil",
            mediaType = MediaType.ANIMATION
        )
    )
    
    /**
     * Obtém as categorias para um tipo de mídia específico
     */
    fun getCategoriesForType(mediaType: MediaType): List<CategoryInfo> {
        return CATEGORIES_BY_TYPE[mediaType] ?: emptyList()
    }
    
    /**
     * Obtém o nome de uma categoria pelo ID
     */
    fun getCategoryName(mediaType: MediaType, categoryId: String): String {
        return getCategoriesForType(mediaType)
            .find { it.id == categoryId }
            ?.name ?: categoryId
    }
}

/**
 * Informação básica de categoria
 */
data class CategoryInfo(
    val id: String,
    val name: String,
    val description: String
)

/**
 * Informação de categoria principal
 */
data class MainCategoryInfo(
    val id: String,
    val title: String,
    val description: String,
    val mediaType: MediaType
)