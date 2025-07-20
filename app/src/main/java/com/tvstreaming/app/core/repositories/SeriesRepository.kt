package com.tvstreaming.app.core.repositories

import com.tvstreaming.app.core.api.ApiService
import com.tvstreaming.app.core.utils.Resource
import com.tvstreaming.app.models.ChannelCategory
import com.tvstreaming.app.models.MediaContent
import com.tvstreaming.app.models.MediaContentImpl
import com.tvstreaming.app.models.MediaType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeriesRepository @Inject constructor(
    private val apiService: ApiService
) {
    
    fun getSeriesCategories(): Flow<Resource<List<ChannelCategory>>> = flow {
        emit(Resource.Loading())
        
        try {
            delay(500) // Simula latência de rede
            
            val categories = listOf(
                ChannelCategory("drama", "Drama", "🎭"),
                ChannelCategory("comedy", "Comédia", "😂"),
                ChannelCategory("action", "Ação & Aventura", "⚡"),
                ChannelCategory("scifi", "Ficção Científica", "🚀"),
                ChannelCategory("thriller", "Suspense", "🔪"),
                ChannelCategory("documentary", "Documentário", "📺")
            )
            
            emit(Resource.Success(categories))
        } catch (e: Exception) {
            emit(Resource.Error(e, "Erro ao carregar categorias"))
        }
    }
    
    fun getFeaturedSeries(): Flow<Resource<MediaContent>> = flow {
        emit(Resource.Loading())
        
        try {
            delay(300)
            
            val featuredSeries = MediaContentImpl(
                id = "featured_series_1",
                title = "The Last of Us",
                description = "Vinte anos depois da destruição da civilização moderna, Joel, um sobrevivente endurecido, é contratado para contrabandear Ellie, uma garota de 14 anos, para fora de uma zona de quarentena opressiva.",
                posterUrl = "https://image.tmdb.org/t/p/w500/uKvVjHNqB5VmOrdxqAt2F7J78ED.jpg",
                backdropUrl = "https://image.tmdb.org/t/p/original/56v2KjBlU4XaOv9rVYEQypROD7P.jpg",
                rating = "8.7",
                year = "2023",
                duration = 60,
                mediaType = MediaType.SERIES,
                seasonCount = 1,
                episodeCount = 9
            )
            
            emit(Resource.Success(featuredSeries))
        } catch (e: Exception) {
            emit(Resource.Error(e, "Erro ao carregar série em destaque"))
        }
    }
    
    fun getSeriesByCategory(categoryId: String): Flow<Resource<List<MediaContent>>> = flow {
        emit(Resource.Loading())
        
        try {
            delay(500)
            
            val series = when (categoryId) {
                "drama" -> createDramaSeries()
                "comedy" -> createComedySeries()
                "action" -> createActionSeries()
                "scifi" -> createSciFiSeries()
                "thriller" -> createThrillerSeries()
                "documentary" -> createDocumentarySeries()
                else -> emptyList()
            }
            
            emit(Resource.Success(series))
        } catch (e: Exception) {
            emit(Resource.Error(e, "Erro ao carregar séries"))
        }
    }
    
    private fun createDramaSeries(): List<MediaContent> = listOf(
        MediaContentImpl(
            id = "drama_1",
            title = "Breaking Bad",
            description = "Um professor de química do ensino médio com câncer terminal se une a um ex-aluno para fabricar e vender metanfetamina.",
            posterUrl = "https://image.tmdb.org/t/p/w500/3xnWaLQjelJDDF7LT1WBo6f4BRe.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/tsRy63Mu5cu8etL1X7ZLyf7UP1M.jpg",
            rating = "9.5",
            year = "2008",
            duration = 47,
            mediaType = MediaType.SERIES,
            seasonCount = 5,
            episodeCount = 62
        ),
        MediaContentImpl(
            id = "drama_2",
            title = "The Crown",
            description = "A história da Rainha Elizabeth II e os eventos políticos e pessoais que moldaram o segundo reinado mais longo da história britânica.",
            posterUrl = "https://image.tmdb.org/t/p/w500/1M876KPjulVwppEpldhdc8V4o68.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/qsD5OHqW7DSnaQ2afwz8Ptht1Xb.jpg",
            rating = "8.6",
            year = "2016",
            duration = 58,
            mediaType = MediaType.SERIES,
            seasonCount = 6,
            episodeCount = 60
        ),
        MediaContentImpl(
            id = "drama_3",
            title = "Succession",
            description = "A saga da família Roy, donos de um dos maiores conglomerados de mídia e entretenimento do mundo.",
            posterUrl = "https://image.tmdb.org/t/p/w500/e2X32jUfJ2kb4QtNg1lCJGGsWF8.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/5JeA5bvXgr9rh7ysOlKkWNPFMU1.jpg",
            rating = "8.8",
            year = "2018",
            duration = 60,
            mediaType = MediaType.SERIES,
            seasonCount = 4,
            episodeCount = 39
        )
    )
    
    private fun createComedySeries(): List<MediaContent> = listOf(
        MediaContentImpl(
            id = "comedy_1",
            title = "The Office",
            description = "Um mockumentário sobre um grupo de trabalhadores de escritório típicos, onde a rotina de trabalho consiste em conflitos de ego e comportamento inadequado.",
            posterUrl = "https://image.tmdb.org/t/p/w500/qWnJzyZhyy74gjpSjIXWmuk0ifX.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/s7XRGK6eYQQRReaLO4a2F9Nap51.jpg",
            rating = "8.9",
            year = "2005",
            duration = 22,
            mediaType = MediaType.SERIES,
            seasonCount = 9,
            episodeCount = 201
        ),
        MediaContentImpl(
            id = "comedy_2",
            title = "Brooklyn Nine-Nine",
            description = "Jake Peralta, um detetive imaturo mas talentoso no 99º distrito do Brooklyn, entra em conflito com seu novo comandante rígido.",
            posterUrl = "https://image.tmdb.org/t/p/w500/hgRMSOt7a1b8qyQR68vUixJPang.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/JNVjRiLZHi3bQ0pvJ3Js9jnZoe.jpg",
            rating = "8.4",
            year = "2013",
            duration = 21,
            mediaType = MediaType.SERIES,
            seasonCount = 8,
            episodeCount = 153
        )
    )
    
    private fun createActionSeries(): List<MediaContent> = listOf(
        MediaContentImpl(
            id = "action_1",
            title = "The Boys",
            description = "Um grupo de vigilantes se propõe a derrubar super-heróis corruptos que abusam de seus superpoderes.",
            posterUrl = "https://image.tmdb.org/t/p/w500/stTEycfG9928HYGEISBFaG1ngjM.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/mGVrXeIjyecj6TKmwPVpHlscEmw.jpg",
            rating = "8.7",
            year = "2019",
            duration = 60,
            mediaType = MediaType.SERIES,
            seasonCount = 4,
            episodeCount = 32
        ),
        MediaContentImpl(
            id = "action_2",
            title = "The Mandalorian",
            description = "Após a queda do Império, um caçador de recompensas solitário percorre a galáxia com uma criança misteriosa.",
            posterUrl = "https://image.tmdb.org/t/p/w500/sWgBv7LV2PRoQgkxwlibdGXKz1S.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/o7qi2v4uWQ8bZ1tW3KI0Ztn2epk.jpg",
            rating = "8.5",
            year = "2019",
            duration = 40,
            mediaType = MediaType.SERIES,
            seasonCount = 3,
            episodeCount = 24
        )
    )
    
    private fun createSciFiSeries(): List<MediaContent> = listOf(
        MediaContentImpl(
            id = "scifi_1",
            title = "Stranger Things",
            description = "Quando um garoto desaparece, a cidade descobre experimentos secretos, forças sobrenaturais e uma garota estranha.",
            posterUrl = "https://image.tmdb.org/t/p/w500/49WJfeN0moxb9IPfGn8AIqMGskD.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/56v2KjBlU4XaOv9rVYEQypROD7P.jpg",
            rating = "8.7",
            year = "2016",
            duration = 51,
            mediaType = MediaType.SERIES,
            seasonCount = 4,
            episodeCount = 42
        ),
        MediaContentImpl(
            id = "scifi_2",
            title = "Black Mirror",
            description = "Uma antologia que explora o lado sombrio da tecnologia e como ela afeta a sociedade moderna.",
            posterUrl = "https://image.tmdb.org/t/p/w500/7PRddO7z7mcPi21nZTCMGShAyy1.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/b92npz1sZwvmjUfVIrWXehrk3pc.jpg",
            rating = "8.8",
            year = "2011",
            duration = 60,
            mediaType = MediaType.SERIES,
            seasonCount = 6,
            episodeCount = 27
        )
    )
    
    private fun createThrillerSeries(): List<MediaContent> = listOf(
        MediaContentImpl(
            id = "thriller_1",
            title = "Dark",
            description = "Segredos de família se desenrolam quando o desaparecimento de duas crianças em uma cidade alemã revela relações entre quatro famílias.",
            posterUrl = "https://image.tmdb.org/t/p/w500/apbrbWs8M9lyOpJYU5WXrpFbk1Z.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/tsRy63Mu5cu8etL1X7ZLyf7UP1M.jpg",
            rating = "8.7",
            year = "2017",
            duration = 60,
            mediaType = MediaType.SERIES,
            seasonCount = 3,
            episodeCount = 26
        )
    )
    
    private fun createDocumentarySeries(): List<MediaContent> = listOf(
        MediaContentImpl(
            id = "doc_1",
            title = "Our Planet",
            description = "Documentário sobre a natureza que explora as maravilhas naturais do planeta e como as mudanças climáticas impactam todas as criaturas vivas.",
            posterUrl = "https://image.tmdb.org/t/p/w500/OjHhm5STfcWjkBYhvuLFj0VSz.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/Aucfl8Fjhle4gNjBGhkOzwXhttZ.jpg",
            rating = "8.9",
            year = "2019",
            duration = 48,
            mediaType = MediaType.SERIES,
            seasonCount = 1,
            episodeCount = 8
        )
    )
}