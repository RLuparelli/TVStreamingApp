package com.tvstreaming.app.models

import com.google.gson.annotations.SerializedName

data class Episode(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("thumbnail")
    val thumbnail: String? = null,
    @SerializedName("duration")
    val duration: Int? = null, // in minutes
    @SerializedName("season")
    val season: Int,
    @SerializedName("episode_number")
    val episodeNumber: Int,
    @SerializedName("stream_url")
    val streamUrl: String? = null
) {
    fun getFormattedTitle(): String {
        return "T${season}:E${episodeNumber} - $title"
    }
    
    fun getFormattedDuration(): String {
        return duration?.let { "${it}min" } ?: ""
    }
}

data class Season(
    @SerializedName("season_number")
    val seasonNumber: Int,
    @SerializedName("episodes")
    val episodes: List<Episode>
)