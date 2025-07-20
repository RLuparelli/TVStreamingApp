package com.tvstreaming.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tvstreaming.app.ui.theme.isTelevision

@Composable
fun ErrorScreen(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isTV = configuration.isTelevision()
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(if (isTV) 48.dp else 24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                modifier = Modifier.size(if (isTV) 80.dp else 64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            
            Spacer(modifier = Modifier.height(if (isTV) 24.dp else 16.dp))
            
            Text(
                text = message,
                style = if (isTV) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            onRetry?.let {
                Spacer(modifier = Modifier.height(if (isTV) 32.dp else 24.dp))
                
                Button(
                    onClick = it,
                    modifier = Modifier.padding(horizontal = if (isTV) 24.dp else 16.dp)
                ) {
                    Text(
                        text = "Try Again",
                        style = if (isTV) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}