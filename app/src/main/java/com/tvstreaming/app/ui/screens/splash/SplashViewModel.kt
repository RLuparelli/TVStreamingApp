package com.tvstreaming.app.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tvstreaming.app.core.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import timber.log.Timber

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()
    
    init {
        checkAuthStatus()
    }
    
    private fun checkAuthStatus() {
        viewModelScope.launch {
            Timber.d("SplashViewModel: Starting auth check")
            // Minimum splash duration for branding
            delay(1500)
            
            try {
                withTimeout(5000) { // 5 second timeout
                    val isAuthenticated = authRepository.isAuthenticated()
                    Timber.d("SplashViewModel: Is authenticated = $isAuthenticated")
                    
                    if (isAuthenticated) {
                        Timber.d("SplashViewModel: Navigating to Home")
                        _uiState.value = SplashUiState.NavigateToHome
                    } else {
                        Timber.d("SplashViewModel: Navigating to Auth")
                        _uiState.value = SplashUiState.NavigateToAuth
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "SplashViewModel: Error checking auth status")
                // On any error, navigate to auth screen
                _uiState.value = SplashUiState.NavigateToAuth
            }
        }
    }
}

sealed class SplashUiState {
    object Loading : SplashUiState()
    object NavigateToAuth : SplashUiState()
    object NavigateToHome : SplashUiState()
    data class Error(val message: String) : SplashUiState()
}