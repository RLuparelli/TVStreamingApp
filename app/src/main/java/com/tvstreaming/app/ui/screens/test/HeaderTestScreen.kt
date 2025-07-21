package com.tvstreaming.app.ui.screens.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tvstreaming.app.ui.components.HeaderStyle
import com.tvstreaming.app.ui.components.HomeHeader

@Composable
fun HeaderTestScreen() {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val screenHeightDp = configuration.screenHeightDp
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Info sobre a tela
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Informações da Tela",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Largura: ${screenWidthDp}dp",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Altura: ${screenHeightDp}dp",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Orientação: ${if (isLandscape) "Paisagem" else "Retrato"}",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Compacto: ${if (screenWidthDp < 600) "Sim" else "Não"}",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            }
        }
        
        // Professional Header
        item {
            Column {
                Text(
                    text = "Professional Header",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                HomeHeader(
                    userName = "Usuário",
                    onSearchClick = {},
                    style = HeaderStyle.PROFESSIONAL,
                    isTV = false
                )
            }
        }
        
        // Colorful Header
        item {
            Column {
                Text(
                    text = "Colorful Header",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                HomeHeader(
                    userName = "Usuário",
                    onSearchClick = {},
                    style = HeaderStyle.COLORFUL,
                    isTV = false
                )
            }
        }
        
        // Minimal Header
        item {
            Column {
                Text(
                    text = "Minimal Header",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                HomeHeader(
                    userName = "Usuário",
                    onSearchClick = {},
                    style = HeaderStyle.MINIMAL,
                    isTV = false
                )
            }
        }
        
        // Modern Header
        item {
            Column {
                Text(
                    text = "Modern Header",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                HomeHeader(
                    userName = "Usuário",
                    onSearchClick = {},
                    style = HeaderStyle.MODERN,
                    isTV = false
                )
            }
        }
        
        // Espaço no final
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}