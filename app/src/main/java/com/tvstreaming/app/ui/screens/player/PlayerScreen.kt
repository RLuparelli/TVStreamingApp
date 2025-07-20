package com.tvstreaming.app.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun PlayerScreen(
    contentId: String,
    onNavigateBack: () -> Unit
) {
    // TODO: Implement player screen with ExoPlayer
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Player Screen - Content ID: $contentId",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}