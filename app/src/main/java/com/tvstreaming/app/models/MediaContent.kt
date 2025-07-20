package com.tvstreaming.app.models

import com.google.gson.annotations.SerializedName

/**
 * Interface base para todo tipo de conteúdo de mídia
 * Seguindo Interface Segregation Principle (ISP)
 */
interface MediaContent {
    val id: String
    val title: String
    val description: String?
    val posterUrl: String
    val backdropUrl: String?
    val rating: String?
    val year: String?
    val duration: Int? // em minutos
    val mediaType: MediaType
}

/**
 * Enum para tipos de mídia
 */
enum class MediaType {
    @SerializedName("movie")
    MOVIE,
    
    @SerializedName("series")
    SERIES,
    
    @SerializedName("animation")
    ANIMATION,
    
    @SerializedName("live")
    LIVE
}

/**
 * Implementação genérica de MediaContent
 * Pode ser usada para qualquer tipo de conteúdo
 */
data class MediaContentImpl(
    override val id: String,
    override val title: String,
    override val description: String? = null,
    override val posterUrl: String,
    override val backdropUrl: String? = null,
    override val rating: String? = null,
    override val year: String? = null,
    override val duration: Int? = null,
    override val mediaType: MediaType,
    
    // Campos adicionais específicos
    val genre: String? = null,
    val director: String? = null,
    val cast: String? = null,
    val trailerUrl: String? = null,
    val streamUrl: String? = null,
    val seasonCount: Int? = null, // Para séries
    val episodeCount: Int? = null, // Para séries
    val ageRating: String? = null // Para desenhos
) : MediaContent

/**
 * Extension functions para converter Movie para MediaContent
 */
fun Movie.toMediaContent(): MediaContent = MediaContentImpl(
    id = id,
    title = title,
    description = description,
    posterUrl = posterUrl,
    backdropUrl = backdropUrl,
    rating = rating,
    year = year,
    duration = duration,
    mediaType = MediaType.MOVIE,
    genre = genre
)

/**
 * Extension para converter Content para MediaContent
 */
fun Content.toMediaContent(): MediaContent = MediaContentImpl(
    id = id,
    title = title,
    description = description,
    posterUrl = posterUrl,
    backdropUrl = backdropUrl,
    rating = rating,
    year = releaseDate?.take(4), // Pega só o ano
    duration = duration,
    mediaType = when (type) {
        "movie" -> MediaType.MOVIE
        "series" -> MediaType.SERIES
        "animation" -> MediaType.ANIMATION
        "live" -> MediaType.LIVE
        else -> MediaType.MOVIE
    },
    streamUrl = streamUrl
)

/**
 * Data class temporária Movie agora implementa MediaContent
 * Mantida para compatibilidade durante refatoração
 */
data class Movie(
    override val id: String,
    override val title: String,
    override val description: String? = null,
    override val posterUrl: String,
    override val backdropUrl: String? = null,
    override val rating: String? = null,
    override val year: String? = null,
    override val duration: Int? = null,
    val genre: String? = null
) : MediaContent {
    override val mediaType: MediaType = MediaType.MOVIE
}