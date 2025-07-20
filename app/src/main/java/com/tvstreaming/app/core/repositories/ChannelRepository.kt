package com.tvstreaming.app.core.repositories

import com.tvstreaming.app.core.api.ApiService
import com.tvstreaming.app.core.utils.Resource
import com.tvstreaming.app.core.utils.safeApiCall
import com.tvstreaming.app.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChannelRepository @Inject constructor(
    private val apiService: ApiService
) {
    
    // Buscar categorias de canais
    fun getChannelCategories(): Flow<Resource<List<ChannelCategory>>> = flow {
        emit(Resource.Loading())
        
        val result = safeApiCall {
            apiService.getChannelCategories()
        }
        
        when (result) {
            is Resource.Success -> {
                val categories = result.data?.categories ?: emptyList()
                emit(Resource.Success(categories))
            }
            is Resource.Error -> {
                emit(Resource.Error(result.error ?: Exception("Unknown error"), result.message ?: "Unknown error"))
            }
            else -> {}
        }
    }
    
    // Buscar lista de canais
    fun getChannels(
        category: String? = null,
        search: String? = null,
        page: Int = 1
    ): Flow<Resource<ChannelListResponse>> = flow {
        emit(Resource.Loading())
        
        val result = safeApiCall {
            apiService.getChannels(
                category = category,
                search = search,
                page = page,
                limit = 20
            )
        }
        
        emit(result)
    }
    
    // Buscar detalhes do canal
    fun getChannelDetails(channelId: String): Flow<Resource<ChannelDetails>> = flow {
        emit(Resource.Loading())
        
        val result = safeApiCall {
            apiService.getChannelDetails(channelId)
        }
        
        emit(result)
    }
    
    // Buscar canais favoritos (mock local por enquanto)
    fun getFavoriteChannels(): Flow<Resource<List<Channel>>> = flow {
        emit(Resource.Loading())
        
        try {
            // Por enquanto, retorna uma lista vazia
            // Futuramente será implementado com banco de dados local
            emit(Resource.Success(emptyList()))
        } catch (e: Exception) {
            emit(Resource.Error(e, e.message ?: "Erro ao buscar favoritos"))
        }
    }
    
    // Adicionar/remover canal dos favoritos
    suspend fun toggleFavorite(channelId: String): Resource<Boolean> {
        return try {
            // Por enquanto, apenas retorna sucesso
            // Futuramente será implementado com banco de dados local
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e, e.message ?: "Erro ao atualizar favorito")
        }
    }
}