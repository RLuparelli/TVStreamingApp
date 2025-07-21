package com.tvstreaming.app.ui.screens.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeAreaTestScreen() {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val screenHeightDp = configuration.screenHeightDp
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Safe Area Test") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Red.copy(alpha = 0.5f)
                )
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top area - should not be cut off
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.Green.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Top Area - Should be visible",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
            
            // Center content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Screen Info",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Width: ${screenWidthDp}dp",
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "Height: ${screenHeightDp}dp",
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "Orientation: ${if (isLandscape) "Landscape" else "Portrait"}",
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            
            // Bottom area - should not be cut off
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.Blue.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Bottom Area - Should be visible",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    }
}