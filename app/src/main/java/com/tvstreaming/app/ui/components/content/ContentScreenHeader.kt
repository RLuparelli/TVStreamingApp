package com.tvstreaming.app.ui.components.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Header reutilizável para todas as telas de conteúdo
 * Seguindo Single Responsibility Principle
 */
@Composable
fun ContentScreenHeader(
    title: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Black.copy(alpha = 0.6f),
    contentColor: Color = Color.White
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botão voltar
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .size(48.dp)
                .background(
                    backgroundColor,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Voltar",
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Título
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = contentColor,
            fontWeight = FontWeight.Bold
        )
    }
}