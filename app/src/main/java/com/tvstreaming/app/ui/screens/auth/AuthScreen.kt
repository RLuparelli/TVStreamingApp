package com.tvstreaming.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tvstreaming.app.ui.components.common.LoadingScreen
import com.tvstreaming.app.ui.theme.isTelevision

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val isTV = configuration.isTelevision()
    val keyboardController = LocalSoftwareKeyboardController.current
    
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onAuthSuccess()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is AuthUiState.Loading -> LoadingScreen()
            is AuthUiState.Error -> AuthForm(
                isTV = isTV,
                errorMessage = state.message,
                onSubmit = { code ->
                    keyboardController?.hide()
                    viewModel.authenticateWithCode(code)
                }
            )
            else -> AuthForm(
                isTV = isTV,
                onSubmit = { code ->
                    keyboardController?.hide()
                    viewModel.authenticateWithCode(code)
                }
            )
        }
    }
}

@Composable
private fun AuthForm(
    isTV: Boolean,
    errorMessage: String? = null,
    onSubmit: (String) -> Unit
) {
    var activationCode by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    
    LaunchedEffect(Unit) {
        if (isTV) {
            focusRequester.requestFocus()
        }
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(if (isTV) 48.dp else 24.dp)
    ) {
        Text(
            text = "Welcome to TV Streaming",
            style = if (isTV) {
                MaterialTheme.typography.displayMedium
            } else {
                MaterialTheme.typography.headlineLarge
            },
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(if (isTV) 48.dp else 32.dp))
        
        Text(
            text = "Enter your activation code to continue",
            style = if (isTV) {
                MaterialTheme.typography.bodyLarge
            } else {
                MaterialTheme.typography.bodyMedium
            },
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(if (isTV) 32.dp else 24.dp))
        
        OutlinedTextField(
            value = activationCode,
            onValueChange = { activationCode = it.uppercase() },
            label = { Text("Activation Code") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                autoCorrectEnabled = false
            ),
            modifier = Modifier
                .fillMaxWidth(if (isTV) 0.5f else 0.8f)
                .focusRequester(focusRequester),
            textStyle = if (isTV) {
                MaterialTheme.typography.headlineSmall
            } else {
                MaterialTheme.typography.bodyLarge
            }
        )
        
        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(if (isTV) 32.dp else 24.dp))
        
        Button(
            onClick = { onSubmit(activationCode) },
            enabled = activationCode.length >= 4,
            modifier = Modifier
                .fillMaxWidth(if (isTV) 0.3f else 0.5f)
                .height(if (isTV) 56.dp else 48.dp)
        ) {
            Text(
                text = "Activate",
                style = if (isTV) {
                    MaterialTheme.typography.labelLarge
                } else {
                    MaterialTheme.typography.labelMedium
                }
            )
        }
        
        if (!isTV) {
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = "Or use automatic device authentication",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}