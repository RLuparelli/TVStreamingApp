package com.tvstreaming.app.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tvstreaming.app.R
import com.tvstreaming.app.ui.components.LoadingScreen
import com.tvstreaming.app.ui.theme.isTelevision
import timber.log.Timber

@Composable
fun SplashScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val isTV = configuration.isTelevision()
    
    LaunchedEffect(uiState) {
        Timber.d("SplashScreen: UI State changed to $uiState")
        when (uiState) {
            is SplashUiState.NavigateToAuth -> {
                Timber.d("SplashScreen: Calling onNavigateToAuth")
                onNavigateToAuth()
            }
            is SplashUiState.NavigateToHome -> {
                Timber.d("SplashScreen: Calling onNavigateToHome")
                onNavigateToHome()
            }
            else -> Unit
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = if (isTV) {
                    MaterialTheme.typography.displayLarge
                } else {
                    MaterialTheme.typography.displayMedium
                },
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(if (isTV) 48.dp else 32.dp))
            
            when (val state = uiState) {
                is SplashUiState.Loading -> {
                    LoadingScreen()
                }
                is SplashUiState.Error -> {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> Unit
            }
        }
    }
}