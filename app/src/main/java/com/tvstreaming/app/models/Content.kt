package com.tvstreaming.app.models

import com.google.gson.annotations.SerializedName

data class Content(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("poster_url")
    val posterUrl: String,
    
    @SerializedName("backdrop_url")
    val backdropUrl: String? = null,
    
    @SerializedName("stream_url")
    val streamUrl: String,
    
    @SerializedName("type")
    val type: String,
    
    @SerializedName("thumbnail_url")
    val thumbnailUrl: String? = null,
    
    @SerializedName("content_url")
    val contentUrl: String? = null,
    
    @SerializedName("duration")
    val duration: Int? = null, // in minutes
    
    @SerializedName("rating")
    val rating: String? = null,
    
    @SerializedName("release_date")
    val releaseDate: String? = null,
    
    @SerializedName("genres")
    val genres: List<String>? = null,
    
    @SerializedName("is_premium")
    val isPremium: Boolean = false,
    
    @SerializedName("is_favorite")
    val isFavorite: Boolean = false
)

enum class ContentType {
    @SerializedName("movie")
    MOVIE,
    
    @SerializedName("series")
    SERIES,
    
    @SerializedName("live")
    LIVE
}

// Extension function to convert ContentItem to Content
fun ContentItem.toContent(): Content {
    return Content(
        id = streamId,
        title = name,
        description = "",
        posterUrl = streamIcon ?: "",
        backdropUrl = streamIcon,
        streamUrl = "", // Will be generated dynamically
        type = streamType,
        thumbnailUrl = streamIcon,
        contentUrl = "",
        duration = null,
        rating = rating,
        releaseDate = added,
        genres = emptyList(),
        isPremium = false,
        isFavorite = false
    )
}