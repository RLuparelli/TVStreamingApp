package com.tvstreaming.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.tvstreaming.app.ui.theme.isTelevision

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isTV = configuration.isTelevision()
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier,
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = if (isTV) 6.dp else 4.dp
        )
    }
}