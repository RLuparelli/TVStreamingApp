package com.tvstreaming.app.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateToPlayer: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search movies and series") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // TODO: Implement search results
            Text(
                text = "Search functionality coming soon",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}