package com.tvstreaming.app.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// Resposta de autenticação
data class AuthResponse(
    @SerializedName("user_info")
    val userInfo: UserInfo,
    @SerializedName("server_info")
    val serverInfo: ServerInfo
)

// Informações do usuário
data class UserInfo(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("auth")
    val auth: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("exp_date")
    val expDate: String,
    @SerializedName("is_trial")
    val isTrial: String,
    @SerializedName("active_cons")
    val activeCons: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("max_connections")
    val maxConnections: String,
    @SerializedName("allowed_output_formats")
    val allowedOutputFormats: List<String>
) : Serializable

// Informações do servidor
data class ServerInfo(
    @SerializedName("xui")
    val xui: Boolean,
    @SerializedName("version")
    val version: String,
    @SerializedName("revision")
    val revision: Int,
    @SerializedName("url")
    val url: String,
    @SerializedName("port")
    val port: String,
    @SerializedName("https_port")
    val httpsPort: String?,
    @SerializedName("server_protocol")
    val serverProtocol: String,
    @SerializedName("rtmp_port")
    val rtmpPort: String,
    @SerializedName("timestamp_now")
    val timestampNow: Long,
    @SerializedName("time_now")
    val timeNow: String,
    @SerializedName("timezone")
    val timezone: String,
    @SerializedName("tenant_id")
    val tenantId: String? = null
) : Serializable

// Categoria de conteúdo
data class Category(
    @SerializedName("category_id")
    val categoryId: String,
    @SerializedName("category_name")
    val categoryName: String,
    @SerializedName("parent_id")
    val parentId: Int
) : Serializable {

    // Método para extrair tipo de categoria
    fun getCategoryType(): CategoryType {
        return when {
            categoryName.contains("Filmes", ignoreCase = true) -> CategoryType.MOVIES
            categoryName.contains("Serie", ignoreCase = true) ||
                    categoryName.contains("Série", ignoreCase = true) -> CategoryType.SERIES
            categoryName.contains("Documentario", ignoreCase = true) ||
                    categoryName.contains("Documentário", ignoreCase = true) -> CategoryType.DOCUMENTARIES
            categoryName.contains("Show", ignoreCase = true) -> CategoryType.SHOWS
            categoryName.contains("Stand Up", ignoreCase = true) -> CategoryType.STANDUP
            categoryName.contains("Esporte", ignoreCase = true) -> CategoryType.SPORTS
            else -> CategoryType.OTHER
        }
    }

    // Método para obter ícone da categoria
    fun getCategoryIcon(): Int {
        return when (getCategoryType()) {
            CategoryType.MOVIES -> android.R.drawable.ic_menu_gallery
            CategoryType.SERIES -> android.R.drawable.ic_menu_slideshow
            CategoryType.DOCUMENTARIES -> android.R.drawable.ic_menu_info_details
            CategoryType.SHOWS -> android.R.drawable.star_on
            CategoryType.STANDUP -> android.R.drawable.ic_dialog_info
            CategoryType.SPORTS -> android.R.drawable.ic_menu_manage
            CategoryType.OTHER -> android.R.drawable.ic_menu_help
        }
    }
}

// Enum para tipos de categoria
enum class CategoryType {
    MOVIES,
    SERIES,
    DOCUMENTARIES,
    SHOWS,
    STANDUP,
    SPORTS,
    OTHER
}

// Item de conteúdo (filme, série, etc.)
data class ContentItem(
    @SerializedName("stream_id")
    val streamId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("stream_type")
    val streamType: String,
    @SerializedName("stream_icon")
    val streamIcon: String?,
    @SerializedName("rating")
    val rating: String?,
    @SerializedName("category_id")
    val categoryId: String,
    @SerializedName("container_extension")
    val containerExtension: String?,
    @SerializedName("added")
    val added: String?,
    @SerializedName("custom_sid")
    val customSid: String?,
    @SerializedName("direct_source")
    val directSource: String?
) : Serializable {

    // Método para gerar URL de reprodução
    fun getStreamUrl(userInfo: UserInfo, serverInfo: ServerInfo): String {
        val protocol = serverInfo.serverProtocol
        val url = serverInfo.url
        val port = if (serverInfo.port != "80") ":${serverInfo.port}" else ""
        val username = userInfo.username
        val password = userInfo.password

        return when (streamType) {
            "movie" -> "$protocol://$url$port/movie/$username/$password/$streamId.${containerExtension ?: "mp4"}"
            "series" -> "$protocol://$url$port/series/$username/$password/$streamId.${containerExtension ?: "mp4"}"
            "live" -> "$protocol://$url$port/live/$username/$password/$streamId.${containerExtension ?: "m3u8"}"
            else -> "$protocol://$url$port/movie/$username/$password/$streamId.${containerExtension ?: "mp4"}"
        }
    }
}

// Informações detalhadas do conteúdo
data class ContentDetails(
    @SerializedName("info")
    val info: ContentInfo?,
    @SerializedName("movie_data")
    val movieData: MovieData?
) : Serializable

data class ContentInfo(
    @SerializedName("name")
    val name: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("genre")
    val genre: String?,
    @SerializedName("plot")
    val plot: String?,
    @SerializedName("cast")
    val cast: String?,
    @SerializedName("director")
    val director: String?,
    @SerializedName("releasedate")
    val releaseDate: String?,
    @SerializedName("duration")
    val duration: String?,
    @SerializedName("rating")
    val rating: String?,
    @SerializedName("country")
    val country: String?,
    @SerializedName("cover_big")
    val coverBig: String?,
    @SerializedName("movie_image")
    val movieImage: String?
) : Serializable

data class MovieData(
    @SerializedName("stream_id")
    val streamId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("added")
    val added: String,
    @SerializedName("category_id")
    val categoryId: String,
    @SerializedName("container_extension")
    val containerExtension: String,
    @SerializedName("custom_sid")
    val customSid: String?,
    @SerializedName("direct_source")
    val directSource: String?
) : Serializable