package com.tvstreaming.app.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// Canal de TV
data class Channel(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("number")
    val number: Int,
    @SerializedName("logo")
    val logo: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("currentProgram")
    val currentProgram: String,
    @SerializedName("nextProgram")
    val nextProgram: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("streamUrl")
    val streamUrl: String,
    @SerializedName("isHD")
    val isHD: Boolean = true,
    @SerializedName("isFavorite")
    val isFavorite: Boolean = false,
    @SerializedName("viewerCount")
    val viewerCount: Int? = null,
    @SerializedName("quality")
    val quality: List<String>? = null,
    @SerializedName("audioTracks")
    val audioTracks: List<String>? = null
) : Serializable

// Categoria de canal
data class ChannelCategory(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("icon")
    val icon: String
) : Serializable

// Resposta da lista de canais
data class ChannelListResponse(
    @SerializedName("channels")
    val channels: List<Channel>,
    @SerializedName("total")
    val total: Int,
    @SerializedName("page")
    val page: Int,
    @SerializedName("totalPages")
    val totalPages: Int,
    @SerializedName("categories")
    val categories: List<ChannelCategory>
) : Serializable

// Programação do canal
data class ChannelSchedule(
    @SerializedName("time")
    val time: String,
    @SerializedName("program")
    val program: String,
    @SerializedName("duration")
    val duration: Int
) : Serializable

// EPG (Guia Eletrônico de Programação)
data class ChannelEPG(
    @SerializedName("date")
    val date: String,
    @SerializedName("programs")
    val programs: List<ChannelSchedule>
) : Serializable

// Detalhes completos do canal
data class ChannelDetails(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("number")
    val number: Int,
    @SerializedName("logo")
    val logo: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("currentProgram")
    val currentProgram: String,
    @SerializedName("nextProgram")
    val nextProgram: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("streamUrl")
    val streamUrl: String,
    @SerializedName("isHD")
    val isHD: Boolean = true,
    @SerializedName("isFavorite")
    val isFavorite: Boolean = false,
    @SerializedName("schedule")
    val schedule: List<ChannelSchedule>? = null,
    @SerializedName("viewerCount")
    val viewerCount: Int? = null,
    @SerializedName("quality")
    val quality: List<String>? = null,
    @SerializedName("audioTracks")
    val audioTracks: List<String>? = null,
    @SerializedName("epg")
    val epg: List<ChannelEPG>? = null
) : Serializable