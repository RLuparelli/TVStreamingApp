package com.tvstreaming.app.core.repositories

import com.tvstreaming.app.core.api.ApiService
import com.tvstreaming.app.core.utils.Resource
import com.tvstreaming.app.models.ContentDetails
import com.tvstreaming.app.models.ContentInfo
import com.tvstreaming.app.models.Episode
import com.tvstreaming.app.models.MovieData
import com.tvstreaming.app.models.Season
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentDetailRepository @Inject constructor(
    private val apiService: ApiService
) {
    
    data class ContentDetailInfo(
        val id: String,
        val title: String,
        val description: String,
        val posterUrl: String,
        val backdropUrl: String,
        val rating: String?,
        val year: String?,
        val duration: Int?, // in minutes
        val genre: String?,
        val director: String?,
        val cast: String?,
        val trailerUrl: String?,
        val isMovie: Boolean,
        val seasons: List<Season>? = null
    )
    
    fun getContentDetail(contentId: String): Flow<Resource<ContentDetailInfo>> = flow {
        emit(Resource.Loading())
        
        try {
            // Simulate API delay
            delay(800)
            
            // Mock data based on contentId
            val detailInfo = when (contentId) {
                "1" -> ContentDetailInfo(
                    id = "1",
                    title = "Avatar: O Caminho da Água",
                    description = "Jake Sully vive com sua nova família na lua Pandora. Quando uma ameaça familiar retorna para terminar o que foi começado anteriormente, Jake deve trabalhar com Neytiri e o exército dos Na'vi para proteger seu planeta.",
                    posterUrl = "https://image.tmdb.org/t/p/w500/t6HIqrRAclMCA60NsSmeqe9RmNV.jpg",
                    backdropUrl = "https://image.tmdb.org/t/p/original/s16H6tpK2utvwDtzZ8Qy4qm5Emw.jpg",
                    rating = "7.8",
                    year = "2022",
                    duration = 192,
                    genre = "Ação, Aventura, Ficção Científica",
                    director = "James Cameron",
                    cast = "Sam Worthington, Zoe Saldana, Sigourney Weaver, Stephen Lang",
                    trailerUrl = "https://www.youtube.com/watch?v=d9MyW72ELq0",
                    isMovie = true
                )
                "2" -> ContentDetailInfo(
                    id = "2",
                    title = "The Last of Us",
                    description = "Vinte anos depois da destruição da civilização moderna, Joel, um sobrevivente endurecido, é contratado para contrabandear Ellie, uma garota de 14 anos, para fora de uma zona de quarentena opressiva. O que começa como um pequeno trabalho logo se torna uma jornada brutal e comovente.",
                    posterUrl = "https://image.tmdb.org/t/p/w500/uKvVjHNqB5VmOrdxqAt2F7J78ED.jpg",
                    backdropUrl = "https://image.tmdb.org/t/p/original/56v2KjBlU4XaOv9rVYEQypROD7P.jpg",
                    rating = "8.7",
                    year = "2023",
                    duration = null,
                    genre = "Drama, Ação & Aventura",
                    director = "Craig Mazin, Neil Druckmann",
                    cast = "Pedro Pascal, Bella Ramsey, Anna Torv, Gabriel Luna",
                    trailerUrl = "https://www.youtube.com/watch?v=uLtkt8BonwM",
                    isMovie = false,
                    seasons = listOf(
                        Season(
                            seasonNumber = 1,
                            episodes = listOf(
                                Episode(
                                    id = "s1e1",
                                    title = "When You're Lost in the Darkness",
                                    description = "Em 2003, um surto de parasitas mutantes transforma humanos em criaturas agressivas.",
                                    thumbnail = "https://image.tmdb.org/t/p/w500/eNJdgC4BAzNvnl9OJHpkoBqEvlO.jpg",
                                    duration = 81,
                                    season = 1,
                                    episodeNumber = 1
                                ),
                                Episode(
                                    id = "s1e2",
                                    title = "Infected",
                                    description = "Ellie, Joel e Tess se aventuram em Boston abandonada, onde encontram clickers.",
                                    thumbnail = "https://image.tmdb.org/t/p/w500/fQ0wSJbVojMetdLl6Vlz6MpKpju.jpg",
                                    duration = 53,
                                    season = 1,
                                    episodeNumber = 2
                                ),
                                Episode(
                                    id = "s1e3",
                                    title = "Long, Long Time",
                                    description = "Joel e Ellie partem em busca de Bill e Frank. A história de amor deles é revelada.",
                                    thumbnail = "https://image.tmdb.org/t/p/w500/2oqVRnSEh0X2c9VQJfDVO5q3l8X.jpg",
                                    duration = 76,
                                    season = 1,
                                    episodeNumber = 3
                                ),
                                Episode(
                                    id = "s1e4",
                                    title = "Please Hold to My Hand",
                                    description = "Viajando pelo Missouri, Joel e Ellie são forçados a fazer um desvio perigoso.",
                                    thumbnail = "https://image.tmdb.org/t/p/w500/gBoIqRlx0CWf8OMbzgLLfMu5lCl.jpg",
                                    duration = 71,
                                    season = 1,
                                    episodeNumber = 4
                                ),
                                Episode(
                                    id = "s1e5",
                                    title = "Endure and Survive",
                                    description = "Enquanto Henry e Sam se escondem dos revolucionários, eles encontram aliados improváveis.",
                                    thumbnail = "https://image.tmdb.org/t/p/w500/eNiO6UUUdQHQovqAqeCYH68rW7W.jpg",
                                    duration = 59,
                                    season = 1,
                                    episodeNumber = 5
                                ),
                                Episode(
                                    id = "s1e6",
                                    title = "Kin",
                                    description = "Após meses de viagem, Joel e Ellie recebem uma segunda chance.",
                                    thumbnail = "https://image.tmdb.org/t/p/w500/pTDshEg2SsqFWbDyo7pIdh7kFKd.jpg",
                                    duration = 58,
                                    season = 1,
                                    episodeNumber = 6
                                ),
                                Episode(
                                    id = "s1e7",
                                    title = "Left Behind",
                                    description = "Ellie cuida de Joel ferido, relembrando sua vida antes de conhecê-lo.",
                                    thumbnail = "https://image.tmdb.org/t/p/w500/nwfvREOlmJ4olPaNvWHfShcS2WH.jpg",
                                    duration = 55,
                                    season = 1,
                                    episodeNumber = 7
                                ),
                                Episode(
                                    id = "s1e8",
                                    title = "When We Are in Need",
                                    description = "Ellie cruza o caminho de um pregador vingativo.",
                                    thumbnail = "https://image.tmdb.org/t/p/w500/v90f1lDRjHDzewlBBefP3mGJUUC.jpg",
                                    duration = 50,
                                    season = 1,
                                    episodeNumber = 8
                                ),
                                Episode(
                                    id = "s1e9",
                                    title = "Look for the Light",
                                    description = "Um confronto devastador força Joel e Ellie a questionarem tudo.",
                                    thumbnail = "https://image.tmdb.org/t/p/w500/mjCZ4zmFX0Mhcaf4hQmeYjLSNvP.jpg",
                                    duration = 43,
                                    season = 1,
                                    episodeNumber = 9
                                )
                            )
                        )
                    )
                )
                "3" -> ContentDetailInfo(
                    id = "3",
                    title = "Oppenheimer",
                    description = "A história do cientista americano J. Robert Oppenheimer e seu papel no desenvolvimento da bomba atômica durante a Segunda Guerra Mundial.",
                    posterUrl = "https://image.tmdb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg",
                    backdropUrl = "https://image.tmdb.org/t/p/original/fm6KqXpk3M2HVveHwCrBSSBaO0V.jpg",
                    rating = "8.5",
                    year = "2023",
                    duration = 180,
                    genre = "Drama, História, Thriller",
                    director = "Christopher Nolan",
                    cast = "Cillian Murphy, Emily Blunt, Matt Damon, Robert Downey Jr.",
                    trailerUrl = "https://www.youtube.com/watch?v=uYPbbksJxIg",
                    isMovie = true
                )
                else -> ContentDetailInfo(
                    id = contentId,
                    title = "Stranger Things",
                    description = "Quando um garoto desaparece, a cidade descobre experimentos secretos, forças sobrenaturais e uma garota estranha.",
                    posterUrl = "https://image.tmdb.org/t/p/w500/49WJfeN0moxb9IPfGn8AIqMGskD.jpg",
                    backdropUrl = "https://image.tmdb.org/t/p/original/56v2KjBlU4XaOv9rVYEQypROD7P.jpg",
                    rating = "8.7",
                    year = "2016",
                    duration = null,
                    genre = "Drama, Ficção Científica & Fantasia, Mistério",
                    director = "The Duffer Brothers",
                    cast = "Millie Bobby Brown, Finn Wolfhard, Winona Ryder, David Harbour",
                    trailerUrl = null,
                    isMovie = false,
                    seasons = listOf(
                        Season(
                            seasonNumber = 1,
                            episodes = List(8) { index ->
                                Episode(
                                    id = "st_s1e${index + 1}",
                                    title = "Chapter ${index + 1}",
                                    description = "Episode ${index + 1} of Stranger Things Season 1",
                                    thumbnail = "https://image.tmdb.org/t/p/w500/49WJfeN0moxb9IPfGn8AIqMGskD.jpg",
                                    duration = 45 + (index * 5),
                                    season = 1,
                                    episodeNumber = index + 1
                                )
                            }
                        )
                    )
                )
            }
            
            emit(Resource.Success(detailInfo))
        } catch (e: Exception) {
            emit(Resource.Error(e, "Erro ao carregar detalhes do conteúdo"))
        }
    }
}