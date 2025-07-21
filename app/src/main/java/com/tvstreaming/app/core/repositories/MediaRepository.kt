package com.tvstreaming.app.core.repositories

import com.tvstreaming.app.core.api.ApiService
import com.tvstreaming.app.core.utils.Resource
import com.tvstreaming.app.core.utils.safeApiCall
import com.tvstreaming.app.models.*
import com.tvstreaming.app.core.config.CategoryConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Repositório genérico para todos os tipos de mídia
 * Elimina duplicação entre MoviesRepository, SeriesRepository e AnimationRepository
 */
class MediaRepository @Inject constructor(
    private val apiService: ApiService
) {
    /**
     * Busca categorias baseado no tipo de mídia
     */
    fun getCategories(mediaType: MediaType): Flow<Resource<List<ChannelCategory>>> = flow {
        emit(Resource.Loading())
        
        // Por enquanto usando dados mockados, mas estruturado para futura integração com API
        val categoryInfos = CategoryConfig.getCategoriesForType(mediaType)
        val categories = categoryInfos.map { info ->
            ChannelCategory(info.id, info.name, info.description)
        }
        
        emit(Resource.Success(categories))
    }
    
    /**
     * Busca conteúdo em destaque baseado no tipo de mídia
     */
    fun getFeaturedContent(mediaType: MediaType): Flow<Resource<MediaContent?>> = flow {
        emit(Resource.Loading())
        
        val featuredContent = when (mediaType) {
            MediaType.MOVIE -> getFeaturedMovie()
            MediaType.SERIES -> getFeaturedSeries()
            MediaType.ANIMATION -> getFeaturedAnimation()
            MediaType.LIVE -> null
        }
        
        emit(Resource.Success(featuredContent))
    }
    
    /**
     * Busca conteúdo por categoria
     */
    fun getContentByCategory(
        mediaType: MediaType, 
        categoryId: String
    ): Flow<Resource<List<MediaContent>>> = flow {
        emit(Resource.Loading())
        
        val content = when (mediaType) {
            MediaType.MOVIE -> getMoviesByCategory(categoryId)
            MediaType.SERIES -> getSeriesByCategory(categoryId)
            MediaType.ANIMATION -> getAnimationsByCategory(categoryId)
            MediaType.LIVE -> emptyList()
        }
        
        emit(Resource.Success(content))
    }
    
    /**
     * Busca todos os conteúdos organizados por categoria
     */
    fun getAllContentByCategories(mediaType: MediaType): Flow<Resource<Map<String, List<MediaContent>>>> = flow {
        emit(Resource.Loading())
        
        val categoryInfos = CategoryConfig.getCategoriesForType(mediaType)
        val categories = categoryInfos.map { it.id }
        val contentMap = mutableMapOf<String, List<MediaContent>>()
        
        categories.forEach { categoryId ->
            val content = when (mediaType) {
                MediaType.MOVIE -> getMoviesByCategory(categoryId)
                MediaType.SERIES -> getSeriesByCategory(categoryId)
                MediaType.ANIMATION -> getAnimationsByCategory(categoryId)
                MediaType.LIVE -> emptyList()
            }
            
            if (content.isNotEmpty()) {
                val categoryName = CategoryConfig.getCategoryName(mediaType, categoryId)
                contentMap[categoryName] = content
            }
        }
        
        emit(Resource.Success(contentMap))
    }
    
    // Dados mockados - futuramente serão substituídos por chamadas reais à API
    
    private fun getFeaturedMovie(): MediaContent = MediaContentImpl(
        id = "movie_featured",
        title = "Avatar: O Caminho da Água",
        description = "Jake Sully vive com sua nova família na lua Pandora. Quando uma ameaça familiar retorna, Jake deve trabalhar com Neytiri e o exército dos Na'vi para proteger seu planeta.",
        posterUrl = "https://image.tmdb.org/t/p/w500/t6HIqrRAclMCA60NsSmeqe9RmNV.jpg",
        backdropUrl = "https://image.tmdb.org/t/p/original/s16H6tpK2utvwDtzZ8Qy4qm5Emw.jpg",
        streamUrl = "https://example.com/avatar2.mp4",
        duration = 192,
        year = "2022",
        rating = "7.8",
        mediaType = MediaType.MOVIE
    )
    
    private fun getFeaturedSeries(): MediaContent = MediaContentImpl(
        id = "series_featured",
        title = "The Last of Us",
        description = "Em um mundo pós-apocalíptico, um sobrevivente endurecido escolta uma adolescente por um país perigoso e desolado.",
        posterUrl = "https://image.tmdb.org/t/p/w500/uKvVjHNqB5VmOrdxqAt2F7J78ED.jpg",
        backdropUrl = "https://image.tmdb.org/t/p/original/uDgy6hyPd82kOHh6I95FLtLnj6p.jpg",
        streamUrl = "https://example.com/tlou.mp4",
        year = "2023",
        rating = "8.7",
        mediaType = MediaType.SERIES,
        seasonCount = 1,
        episodeCount = 9
    )
    
    private fun getFeaturedAnimation(): MediaContent = MediaContentImpl(
        id = "animation_featured",
        title = "Elementos",
        description = "Em uma cidade onde moradores do fogo, água, terra e ar vivem juntos, uma jovem mulher de fogo e um rapaz que flui descobrem algo fundamental: o quanto têm em comum.",
        posterUrl = "https://image.tmdb.org/t/p/w500/6oH378KUfCEitzJkm07r97L0RsZ.jpg",
        backdropUrl = "https://image.tmdb.org/t/p/original/4fLZUr1e65hKPPVw0R3PmKFKxj1.jpg",
        streamUrl = "https://example.com/elementos.mp4",
        duration = 102,
        year = "2023",
        rating = "7.7",
        mediaType = MediaType.ANIMATION
    )
    
    private fun getMoviesByCategory(categoryId: String): List<MediaContent> {
        // Dados mockados por categoria
        val categoryName = CategoryConfig.getCategoryName(MediaType.MOVIE, categoryId)
        val baseMovies = listOf(
            MediaContentImpl(
                id = "movie_${categoryId}_1",
                title = "Filme de $categoryName 1",
                description = "Descrição do filme",
                posterUrl = "https://picsum.photos/300/450?random=${categoryId}1",
                streamUrl = "https://example.com/movie1.mp4",
                duration = 135,
                year = "2023",
                rating = "7.5",
                mediaType = MediaType.MOVIE
            ),
            MediaContentImpl(
                id = "movie_${categoryId}_2",
                title = "Filme de $categoryName 2",
                description = "Descrição do filme",
                posterUrl = "https://picsum.photos/300/450?random=${categoryId}2",
                streamUrl = "https://example.com/movie2.mp4",
                duration = 118,
                year = "2023",
                rating = "8.2",
                mediaType = MediaType.MOVIE
            )
        )
        
        return baseMovies
    }
    
    private fun getSeriesByCategory(categoryId: String): List<MediaContent> {
        val categoryName = CategoryConfig.getCategoryName(MediaType.SERIES, categoryId)
        val baseSeries = listOf(
            MediaContentImpl(
                id = "series_${categoryId}_1",
                title = "Série de $categoryName 1",
                description = "Descrição da série",
                posterUrl = "https://picsum.photos/300/450?random=series${categoryId}1",
                streamUrl = "https://example.com/series1.mp4",
                year = "2023",
                rating = "8.0",
                mediaType = MediaType.SERIES,
                seasonCount = 3,
                episodeCount = 24
            ),
            MediaContentImpl(
                id = "series_${categoryId}_2",
                title = "Série de $categoryName 2",
                description = "Descrição da série",
                posterUrl = "https://picsum.photos/300/450?random=series${categoryId}2",
                streamUrl = "https://example.com/series2.mp4",
                year = "2023",
                rating = "7.8",
                mediaType = MediaType.SERIES,
                seasonCount = 2,
                episodeCount = 16
            )
        )
        
        return baseSeries
    }
    
    private fun getAnimationsByCategory(categoryId: String): List<MediaContent> {
        val categoryName = CategoryConfig.getCategoryName(MediaType.ANIMATION, categoryId)
        val baseAnimations = listOf(
            MediaContentImpl(
                id = "animation_${categoryId}_1",
                title = "Desenho de $categoryName 1",
                description = "Descrição do desenho",
                posterUrl = "https://picsum.photos/300/450?random=anim${categoryId}1",
                streamUrl = "https://example.com/animation1.mp4",
                duration = 25,
                year = "2023",
                rating = "7.0",
                mediaType = MediaType.ANIMATION
            ),
            MediaContentImpl(
                id = "animation_${categoryId}_2",
                title = "Desenho de $categoryName 2",
                description = "Descrição do desenho",
                posterUrl = "https://picsum.photos/300/450?random=anim${categoryId}2",
                streamUrl = "https://example.com/animation2.mp4",
                duration = 22,
                year = "2023",
                rating = "7.3",
                mediaType = MediaType.ANIMATION
            )
        )
        
        return baseAnimations
    }
}