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
class AnimationRepository @Inject constructor(
    private val apiService: ApiService
) {
    
    fun getAnimationCategories(): Flow<Resource<List<ChannelCategory>>> = flow {
        emit(Resource.Loading())
        
        try {
            delay(500) // Simula latência de rede
            
            val categories = listOf(
                ChannelCategory("disney", "Disney", "🏰"),
                ChannelCategory("pixar", "Pixar", "⭐"),
                ChannelCategory("dreamworks", "DreamWorks", "🎬"),
                ChannelCategory("anime", "Anime", "🎌"),
                ChannelCategory("classic", "Clássicos", "👑"),
                ChannelCategory("educational", "Educativo", "📚")
            )
            
            emit(Resource.Success(categories))
        } catch (e: Exception) {
            emit(Resource.Error(e, "Erro ao carregar categorias"))
        }
    }
    
    fun getFeaturedAnimation(): Flow<Resource<MediaContent>> = flow {
        emit(Resource.Loading())
        
        try {
            delay(300)
            
            val featuredAnimation = MediaContentImpl(
                id = "featured_animation_1",
                title = "Elementos",
                description = "Em uma cidade onde os moradores de fogo, água, terra e ar convivem, uma jovem mulher de fogo e um homem que flui descobrem algo elementar: o quanto eles têm em comum.",
                posterUrl = "https://image.tmdb.org/t/p/w500/d79DeKDCgFOM23O8Dr6MELZVooY.jpg",
                backdropUrl = "https://image.tmdb.org/t/p/original/4Y1WNkd88JXmGfhtWR7dmDAo1T2.jpg",
                rating = "7.8",
                year = "2023",
                duration = 109,
                mediaType = MediaType.ANIMATION,
                ageRating = "Livre"
            )
            
            emit(Resource.Success(featuredAnimation))
        } catch (e: Exception) {
            emit(Resource.Error(e, "Erro ao carregar animação em destaque"))
        }
    }
    
    fun getAnimationsByCategory(categoryId: String): Flow<Resource<List<MediaContent>>> = flow {
        emit(Resource.Loading())
        
        try {
            delay(500)
            
            val animations = when (categoryId) {
                "disney" -> createDisneyAnimations()
                "pixar" -> createPixarAnimations()
                "dreamworks" -> createDreamWorksAnimations()
                "anime" -> createAnimeAnimations()
                "classic" -> createClassicAnimations()
                "educational" -> createEducationalAnimations()
                else -> emptyList()
            }
            
            emit(Resource.Success(animations))
        } catch (e: Exception) {
            emit(Resource.Error(e, "Erro ao carregar animações"))
        }
    }
    
    private fun createDisneyAnimations(): List<MediaContent> = listOf(
        MediaContentImpl(
            id = "disney_1",
            title = "Frozen: Uma Aventura Congelante",
            description = "Uma princesa destemida parte em uma jornada épica ao lado de um homem da montanha, sua rena leal e um boneco de neve para encontrar sua irmã.",
            posterUrl = "https://image.tmdb.org/t/p/w500/nJJLc6rfsNGc3lLNx3FwQZ7gBOy.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/xJWPZIYOEFIjZpBL7SVBGnzRYXp.jpg",
            rating = "7.4",
            year = "2013",
            duration = 102,
            mediaType = MediaType.ANIMATION,
            ageRating = "Livre"
        ),
        MediaContentImpl(
            id = "disney_2",
            title = "Moana: Um Mar de Aventuras",
            description = "Moana, filha do chefe de uma tribo na Oceania, é escolhida pelo oceano para reunir uma relíquia mística com uma deusa.",
            posterUrl = "https://image.tmdb.org/t/p/w500/yJmDBvgRYLK0RbpoF37D40bC12H.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/1qpUk27LVI9UoTS7S0EixUBj5aR.jpg",
            rating = "7.6",
            year = "2016",
            duration = 107,
            mediaType = MediaType.ANIMATION,
            ageRating = "Livre"
        ),
        MediaContentImpl(
            id = "disney_3",
            title = "Encanto",
            description = "Uma jovem colombiana pode ser a última esperança de sua família quando descobre que a magia que envolve Encanto está em perigo.",
            posterUrl = "https://image.tmdb.org/t/p/w500/4j0PNHkMr5ax3IA8tjtxcmPU3QT.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/3G1Q5xF40HkUBJXxt2DQgQzKTp5.jpg",
            rating = "7.7",
            year = "2021",
            duration = 102,
            mediaType = MediaType.ANIMATION,
            ageRating = "Livre"
        )
    )
    
    private fun createPixarAnimations(): List<MediaContent> = listOf(
        MediaContentImpl(
            id = "pixar_1",
            title = "Toy Story",
            description = "Um cowboy de brinquedo fica com ciúmes quando um novo brinquedo espacial se torna o favorito de seu dono.",
            posterUrl = "https://image.tmdb.org/t/p/w500/uXDfjJbdP4ijW5hWSBrPrlKpxab.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/dji4Fm0gCDVb9DQQMRvAI8YNnTz.jpg",
            rating = "8.3",
            year = "1995",
            duration = 81,
            mediaType = MediaType.ANIMATION,
            ageRating = "Livre"
        ),
        MediaContentImpl(
            id = "pixar_2",
            title = "Soul",
            description = "Um músico que perdeu sua paixão pela música é transportado para fora de seu corpo e deve encontrar o caminho de volta com a ajuda de uma alma infantil.",
            posterUrl = "https://image.tmdb.org/t/p/w500/hm58Jw4Lw8OIeECIq5qyPYhAeRJ.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/kf456ZqeC45XTvo6W9pW5clYKfQ.jpg",
            rating = "8.1",
            year = "2020",
            duration = 100,
            mediaType = MediaType.ANIMATION,
            ageRating = "Livre"
        ),
        MediaContentImpl(
            id = "pixar_3",
            title = "Coco",
            description = "O aspirante a músico Miguel, confrontado com a proibição ancestral de sua família à música, entra na Terra dos Mortos para encontrar seu trisavô.",
            posterUrl = "https://image.tmdb.org/t/p/w500/eKi8dIrr8voobbaGzDpe8w0PVbC.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/askg3SMvhqEl4OL52YuvdtY40Yb.jpg",
            rating = "8.4",
            year = "2017",
            duration = 105,
            mediaType = MediaType.ANIMATION,
            ageRating = "Livre"
        )
    )
    
    private fun createDreamWorksAnimations(): List<MediaContent> = listOf(
        MediaContentImpl(
            id = "dreamworks_1",
            title = "Como Treinar o seu Dragão",
            description = "Um jovem viking que aspira a caçar dragões se torna o improvável amigo de um jovem dragão.",
            posterUrl = "https://image.tmdb.org/t/p/w500/ygGmAO60t8GyqUo9xYeYxSZAR3b.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/kXJua1y4RfLRUXkqO9SiRYuxU3u.jpg",
            rating = "8.1",
            year = "2010",
            duration = 98,
            mediaType = MediaType.ANIMATION,
            ageRating = "Livre"
        ),
        MediaContentImpl(
            id = "dreamworks_2",
            title = "Shrek",
            description = "Um ogro mal-humorado deve completar uma missão para salvar uma princesa e recuperar seu pântano.",
            posterUrl = "https://image.tmdb.org/t/p/w500/iB64vpL3dIObOtMZgX3RqdVdQDc.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/sRvXNDItGlWCqtO3j6wks52FmbD.jpg",
            rating = "7.8",
            year = "2001",
            duration = 90,
            mediaType = MediaType.ANIMATION,
            ageRating = "Livre"
        )
    )
    
    private fun createAnimeAnimations(): List<MediaContent> = listOf(
        MediaContentImpl(
            id = "anime_1",
            title = "A Viagem de Chihiro",
            description = "Durante a mudança da família, Chihiro entra em um mundo governado por deuses, bruxas e espíritos, onde humanos são transformados em bestas.",
            posterUrl = "https://image.tmdb.org/t/p/w500/39wmItIWsg5sZMyRUHLkWBcuVCM.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/bSXfU4dwZyBA1vMmXvejdRXBvuF.jpg",
            rating = "8.6",
            year = "2001",
            duration = 125,
            mediaType = MediaType.ANIMATION,
            ageRating = "10 anos"
        ),
        MediaContentImpl(
            id = "anime_2",
            title = "Your Name",
            description = "Dois adolescentes compartilham uma conexão profunda e mágica ao descobrirem que estão trocando de corpos.",
            posterUrl = "https://image.tmdb.org/t/p/w500/q719jXXEzOoYaps6babgKnONONX.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/dIWwZW7dJJtqC6CgWzYkNVKIUm8.jpg",
            rating = "8.4",
            year = "2016",
            duration = 106,
            mediaType = MediaType.ANIMATION,
            ageRating = "12 anos"
        )
    )
    
    private fun createClassicAnimations(): List<MediaContent> = listOf(
        MediaContentImpl(
            id = "classic_1",
            title = "O Rei Leão",
            description = "Simba, um jovem leão, é forçado a fugir após a morte de seu pai, mas retorna adulto para retomar seu lugar como rei.",
            posterUrl = "https://image.tmdb.org/t/p/w500/sKCr78MXSLixwmZ8DyJLrpMsd15.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/1TUg5pO1VZ4B0Q1amk3OlXvlpXV.jpg",
            rating = "8.5",
            year = "1994",
            duration = 88,
            mediaType = MediaType.ANIMATION,
            ageRating = "Livre"
        ),
        MediaContentImpl(
            id = "classic_2",
            title = "A Bela e a Fera",
            description = "Uma jovem prisioneira se apaixona por uma fera monstruosa que na verdade é um príncipe amaldiçoado.",
            posterUrl = "https://image.tmdb.org/t/p/w500/9V4wl4rnUcLfGuTe1J7bYpZqVjy.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/6oNm06TPz2vGiPc2I52oXW3JwPS.jpg",
            rating = "8.0",
            year = "1991",
            duration = 84,
            mediaType = MediaType.ANIMATION,
            ageRating = "Livre"
        )
    )
    
    private fun createEducationalAnimations(): List<MediaContent> = listOf(
        MediaContentImpl(
            id = "edu_1",
            title = "Divertida Mente",
            description = "As emoções de uma jovem garota entram em conflito quando ela e seus pais se mudam para uma nova cidade.",
            posterUrl = "https://image.tmdb.org/t/p/w500/2H1TmgdfNtsKlU9jKdeNyYL5y8T.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/szRvBfK6r2KVED7ycEBuEpcBpSy.jpg",
            rating = "8.1",
            year = "2015",
            duration = 95,
            mediaType = MediaType.ANIMATION,
            ageRating = "Livre"
        ),
        MediaContentImpl(
            id = "edu_2",
            title = "WALL·E",
            description = "No futuro distante, um pequeno robô coletor de lixo embarca em uma jornada espacial que decidirá o destino da humanidade.",
            posterUrl = "https://image.tmdb.org/t/p/w500/hbhFnRzzg6ZDmm8YAmxBnQpQIPh.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/5RwShN5dDKJt711S9LrYWlqxfVW.jpg",
            rating = "8.4",
            year = "2008",
            duration = 98,
            mediaType = MediaType.ANIMATION,
            ageRating = "Livre"
        )
    )
}