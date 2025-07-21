package com.tvstreaming.app.models

/**
 * Adapter para converter entre os modelos Content e MediaContent
 * Temporário até migração completa para MediaContent
 */
object ContentAdapter {
    
    /**
     * Converte Content para MediaContent
     */
    fun Content.toMediaContent(): MediaContent {
        return MediaContentImpl(
            id = this.id,
            title = this.title,
            description = this.description ?: "",
            posterUrl = this.posterUrl,
            backdropUrl = this.backdropUrl,
            streamUrl = this.streamUrl,
            duration = this.duration,
            year = this.releaseDate?.substring(0, 4),
            rating = this.rating,
            mediaType = when (this.type.lowercase()) {
                "movie" -> MediaType.MOVIE
                "series" -> MediaType.SERIES
                "live" -> MediaType.LIVE
                else -> MediaType.MOVIE
            },
            genre = this.genres?.firstOrNull()
        )
    }
    
    /**
     * Converte lista de Content para lista de MediaContent
     */
    fun List<Content>.toMediaContentList(): List<MediaContent> {
        return this.map { it.toMediaContent() }
    }
}