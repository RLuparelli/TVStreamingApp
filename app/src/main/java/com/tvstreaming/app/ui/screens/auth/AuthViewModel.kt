package com.tvstreaming.app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tvstreaming.app.core.auth.AuthRepository
import com.tvstreaming.app.core.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    init {
        // Don't auto-authenticate for now - let user enter code
        // authenticateDevice()
    }
    
    private fun authenticateDevice() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            
            when (val result = authRepository.authenticateDevice()) {
                is Resource.Success -> {
                    _uiState.value = AuthUiState.Success
                }
                is Resource.Error -> {
                    _uiState.value = AuthUiState.Error(
                        result.message ?: "Authentication failed"
                    )
                }
                is Resource.Loading -> {
                    _uiState.value = AuthUiState.Loading
                }
            }
        }
    }
    
    fun authenticateWithCode(code: String) {
        if (code.isBlank() || code.length < 4) {
            _uiState.value = AuthUiState.Error("Invalid activation code")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            
            when (val result = authRepository.authenticateWithCode(code)) {
                is Resource.Success -> {
                    _uiState.value = AuthUiState.Success
                }
                is Resource.Error -> {
                    _uiState.value = AuthUiState.Error(
                        result.message ?: "Authentication failed"
                    )
                }
                is Resource.Loading -> {
                    _uiState.value = AuthUiState.Loading
                }
            }
        }
    }
}

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}