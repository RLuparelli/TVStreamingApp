package com.tvstreaming.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Card as TvCard
import androidx.tv.material3.CardDefaults as TvCardDefaults
import coil.compose.AsyncImage
import com.tvstreaming.app.models.Content
import com.tvstreaming.app.ui.theme.TVCardBackground
import com.tvstreaming.app.ui.theme.TVFocusBorder
import com.tvstreaming.app.ui.theme.isTelevision

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ContentCard(
    content: Content,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isTV = configuration.isTelevision()
    
    if (isTV) {
        TVContentCard(content = content, onClick = onClick, modifier = modifier)
    } else {
        MobileContentCard(content = content, onClick = onClick, modifier = modifier)
    }
}

@Composable
private fun MobileContentCard(
    content: Content,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .width(150.dp)
            .height(225.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            AsyncImage(
                model = content.posterUrl,
                contentDescription = content.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
            )
            
            // Title
            Text(
                text = content.title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TVContentCard(
    content: Content,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    
    TvCard(
        onClick = onClick,
        modifier = modifier
            .width(200.dp)
            .height(300.dp)
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused },
        shape = TvCardDefaults.shape(shape = RoundedCornerShape(12.dp)),
        border = if (isFocused) {
            TvCardDefaults.border(
                focusedBorder = androidx.tv.material3.Border(
                    border = androidx.compose.foundation.BorderStroke(
                        width = 3.dp,
                        color = TVFocusBorder
                    )
                )
            )
        } else {
            TvCardDefaults.border()
        },
        scale = TvCardDefaults.scale(
            focusedScale = 1.05f
        ),
        colors = TvCardDefaults.colors(
            containerColor = TVCardBackground
        )
    ) {
        Box {
            AsyncImage(
                model = content.posterUrl,
                contentDescription = content.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.9f)
                            )
                        )
                    )
            )
            
            // Title
            Text(
                text = content.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            )
        }
    }
}