package com.tvstreaming.app.ui.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tvstreaming.app.core.utils.Resource

/**
 * Componente genérico para gerenciar estados de carregamento/erro/sucesso
 * Segue o princípio DRY evitando duplicação de lógica de estados
 * 
 * @param state Estado atual do recurso
 * @param onRetry Função para tentar novamente em caso de erro
 * @param loadingContent Conteúdo customizado para estado de loading (opcional)
 * @param errorContent Conteúdo customizado para estado de erro (opcional)
 * @param emptyContent Conteúdo para quando não há dados (opcional)
 * @param content Conteúdo principal quando há dados
 */
@Composable
fun <T> StateHandler(
    state: Resource<T>,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    loadingContent: @Composable (() -> Unit)? = null,
    errorContent: @Composable ((String) -> Unit)? = null,
    emptyContent: @Composable (() -> Unit)? = null,
    content: @Composable (T) -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is Resource.Loading -> {
                if (loadingContent != null) {
                    loadingContent()
                } else {
                    DefaultLoadingContent()
                }
            }
            
            is Resource.Error -> {
                if (errorContent != null) {
                    errorContent(state.message ?: "Erro desconhecido")
                } else {
                    DefaultErrorContent(
                        message = state.message ?: "Erro desconhecido",
                        onRetry = onRetry
                    )
                }
            }
            
            is Resource.Success -> {
                val data = state.data
                if (data == null || (data is Collection<*> && data.isEmpty())) {
                    if (emptyContent != null) {
                        emptyContent()
                    } else {
                        DefaultEmptyContent()
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize()) {
                        content(data)
                    }
                }
            }
        }
    }
}

/**
 * Conteúdo padrão para estado de carregamento
 */
@Composable
private fun DefaultLoadingContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Carregando...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

/**
 * Conteúdo padrão para estado de erro
 */
@Composable
private fun DefaultErrorContent(
    message: String,
    onRetry: (() -> Unit)?
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(32.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Erro",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Ops! Algo deu errado",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        
        if (onRetry != null) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tentar Novamente")
            }
        }
    }
}

/**
 * Conteúdo padrão para estado vazio
 */
@Composable
private fun DefaultEmptyContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(32.dp)
    ) {
        Text(
            text = "Nenhum conteúdo disponível",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

/**
 * Versão simplificada do StateHandler para casos comuns
 */
@Composable
fun <T> SimpleStateHandler(
    state: Resource<T>,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) {
    StateHandler(
        state = state,
        onRetry = onRetry,
        modifier = modifier,
        content = content
    )
}