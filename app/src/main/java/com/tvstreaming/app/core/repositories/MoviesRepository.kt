package com.tvstreaming.app.core.repositories

import com.tvstreaming.app.core.api.ApiService
import com.tvstreaming.app.core.utils.Resource
import com.tvstreaming.app.models.ChannelCategory
import com.tvstreaming.app.models.MediaContent
import com.tvstreaming.app.models.MediaContentImpl
import com.tvstreaming.app.models.MediaType
import com.tvstreaming.app.models.Movie
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoviesRepository @Inject constructor(
private val apiService: ApiService
) {
    
    // Get movie categories
    fun getMovieCategories(): Flow<Resource<List<ChannelCategory>>> = flow {
        emit(Resource.Loading())
        
        // For now, return hardcoded categories
        // In the future, this will come from the API
        val categories = listOf(
            ChannelCategory("action", "Ação", "💥"),
            ChannelCategory("comedy", "Comédia", "😂"),
            ChannelCategory("drama", "Drama", "🎭"),
            ChannelCategory("horror", "Terror", "👻"),
            ChannelCategory("romance", "Romance", "💕"),
            ChannelCategory("scifi", "Ficção Científica", "🚀"),
            ChannelCategory("animation", "Animação", "🎨"),
            ChannelCategory("documentary", "Documentário", "📹")
        )
        
        emit(Resource.Success(categories))
    }
    
    // Get featured movie
    fun getFeaturedMovie(): Flow<Resource<Movie>> = flow {
        emit(Resource.Loading())
        
        try {
            delay(300)
            
            // Mock featured movie
            val featuredMovie = Movie(
                id = "avatar2",
                title = "Avatar: O Caminho da Água",
                description = "Jake Sully vive com sua nova família na lua Pandora. Quando uma ameaça familiar retorna, Jake deve trabalhar com Neytiri e o exército dos Na'vi para proteger seu planeta.",
                posterUrl = "https://image.tmdb.org/t/p/w500/t6HIqrRAclMCA60NsSmeqe9RmNV.jpg",
                backdropUrl = "https://image.tmdb.org/t/p/original/t6HIqrRAclMCA60NsSmeqe9RmNV.jpg",
                rating = "7.8",
                year = "2022",
                duration = 192,
                genre = "Ficção Científica"
            )
            
            emit(Resource.Success(featuredMovie))
        } catch (e: Exception) {
            emit(Resource.Error(e, "Erro ao carregar filme em destaque"))
        }
    }
    
    // Get movies by category
    fun getMoviesByCategory(categoryId: String): Flow<Resource<List<Movie>>> = flow {
        emit(Resource.Loading())
        
        try {
            delay(500)
            
            // Mock movies data
            val moviesByCategory = when (categoryId) {
            "action" -> listOf(
                Movie("john_wick_4", "John Wick 4: Baba Yaga", "Com o preço por sua cabeça cada vez maior, John Wick leva sua luta contra a Alta Cúpula global.", "https://image.tmdb.org/t/p/w500/rXTqhpkpj6E0YilQ49PK1SSqLhm.jpg", rating = "8.2", year = "2023", duration = 169),
                Movie("fast_x", "Velozes e Furiosos X", "Dom Toretto e sua família enfrentam o adversário mais letal que já encontraram.", "https://image.tmdb.org/t/p/w500/wDWAA5QApz5L5BKfFaaj8HJCAQM.jpg", rating = "7.5", year = "2023", duration = 141),
                Movie("extraction_2", "Resgate 2", "Após sobreviver a ferimentos quase fatais, Tyler Rake está de volta.", "https://image.tmdb.org/t/p/w500/7gKI9hpEMcZUQpNgKrkDzJpbnNS.jpg", rating = "7.8", year = "2023", duration = 122),
                Movie("the_batman", "The Batman", "Batman investiga uma série de assassinatos misteriosos em Gotham.", "https://image.tmdb.org/t/p/w500/3VFI3zbuNhXzx7dIbYdmvBLekyB.jpg", rating = "8.0", year = "2022", duration = 176)
            )
            "comedy" -> listOf(
                Movie("barbie", "Barbie", "Barbie e Ken estão se divertindo no mundo colorido de Barbie Land.", "https://image.tmdb.org/t/p/w500/yRRuLt7sMBEQkHsd1S3KaaofZn7.jpg", rating = "7.5", year = "2023", duration = 114),
                Movie("guardians_3", "Guardiões da Galáxia Vol. 3", "Peter Quill ainda está se recuperando da perda de Gamora.", "https://image.tmdb.org/t/p/w500/r2J02Z2OpNTctfOSN1Ydgii51I3.jpg", rating = "8.1", year = "2023", duration = 150),
                Movie("cocaine_bear", "O Urso do Pó Branco", "Um urso ingere uma grande quantidade de cocaína e entra em fúria.", "https://image.tmdb.org/t/p/w500/gOnmaxHo0412UVr1QM5Nekv1xPi.jpg", rating = "6.5", year = "2023", duration = 95)
            )
            "drama" -> listOf(
                Movie("oppenheimer", "Oppenheimer", "A história de J. Robert Oppenheimer e o desenvolvimento da bomba atômica.", "https://image.tmdb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg", rating = "8.5", year = "2023", duration = 180),
                Movie("flowers_moon", "Assassinos da Lua das Flores", "Membros da tribo Osage são assassinados após descoberta de petróleo.", "https://image.tmdb.org/t/p/w500/dB6Krk806zeqd0YNp2ngQ9zXteH.jpg", rating = "8.2", year = "2023", duration = 206),
                Movie("the_whale", "A Baleia", "Um professor recluso tenta se reconectar com sua filha adolescente.", "https://image.tmdb.org/t/p/w500/jQ0gylJMxWSL490sy0RrPj1Lj7e.jpg", rating = "7.8", year = "2022", duration = 117)
            )
            "horror" -> listOf(
                Movie("scream_6", "Pânico VI", "Os sobreviventes de Ghostface deixam Woodsboro para trás.", "https://image.tmdb.org/t/p/w500/wDWwcktFSFhCfdsIMHq1bLAbDHG.jpg", rating = "7.3", year = "2023", duration = 122),
                Movie("m3gan", "M3GAN", "Uma boneca com inteligência artificial se torna super protetora.", "https://image.tmdb.org/t/p/w500/d9nBoowhjiiYc4FBNtQkPY7c11H.jpg", rating = "7.0", year = "2023", duration = 102),
                Movie("the_nun_2", "A Freira 2", "A irmã Irene mais uma vez fica cara a cara com Valak.", "https://image.tmdb.org/t/p/w500/5gzzkR7y3hnY8AD1wXjCnVlHba5.jpg", rating = "6.8", year = "2023", duration = 110)
            )
            "romance" -> listOf(
                Movie("anyone_but_you", "Todos Menos Você", "Dois ex-namorados fingem ainda estar juntos.", "https://image.tmdb.org/t/p/w500/yRt7MGBElkLQOYRvLTT1b3B1rcp.jpg", rating = "7.2", year = "2023", duration = 103),
                Movie("the_notebook", "Diário de uma Paixão", "Um casal idoso relembra sua história de amor.", "https://image.tmdb.org/t/p/w500/rNzQyW4f8B8cQeg7Dgj3n6eT5k9.jpg", rating = "7.8", year = "2004", duration = 123),
                Movie("titanic", "Titanic", "Um romance floresce no navio condenado.", "https://image.tmdb.org/t/p/w500/9xjZS2rlVxm8SFx8kPC3aIGCOYQ.jpg", rating = "7.9", year = "1997", duration = 194)
            )
            "scifi" -> listOf(
                Movie("dune_2", "Duna: Parte 2", "Paul Atreides se une aos Fremen em sua jornada.", "https://image.tmdb.org/t/p/w500/8b8R8l88Qje9dn9OE8PY05Nxl1X.jpg", rating = "8.3", year = "2024", duration = 166),
                Movie("spider_verse_2", "Homem-Aranha: Através do Aranhaverso", "Miles Morales embarca em uma aventura épica pelo Multiverso.", "https://image.tmdb.org/t/p/w500/xxPXsL8V95dTwL5vHWIIQALkJQS.jpg", rating = "8.7", year = "2023", duration = 140),
                Movie("gotg_3", "Guardiões da Galáxia Vol. 3", "A equipe embarca em uma missão perigosa para salvar Rocket.", "https://image.tmdb.org/t/p/w500/r2J02Z2OpNTctfOSN1Ydgii51I3.jpg", rating = "8.1", year = "2023", duration = 150)
            )
            else -> emptyList()
        }
        
        emit(Resource.Success(moviesByCategory))
        } catch (e: Exception) {
            emit(Resource.Error(e, "Erro ao carregar filmes"))
        }
    }
}