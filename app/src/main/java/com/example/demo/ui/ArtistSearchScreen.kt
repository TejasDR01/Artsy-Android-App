package com.example.demo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demo.models.Artist
import com.example.demo.R
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState

@Composable
fun ArtistSearchScreen(
    query: String,
    results: List<Artist>,
    onArtistClick: (Artist) -> Unit,
    favoritesViewModel: FavoritesViewModel,
    isLoggedIn : Boolean
) {
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (query.length >= 3) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(results) { artist ->
                    ArtistCard(
                        artist = artist,
                        onArtistClick = onArtistClick,
                        favoritesViewModel = favoritesViewModel,
                        isLoggedIn = isLoggedIn
                    )
                }
            }
        }
    }
}

@Composable
fun ArtistCard(
    artist: Artist,
    onArtistClick: (Artist) -> Unit,
    favoritesViewModel: FavoritesViewModel,
    isLoggedIn : Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(8.dp)
            .clickable { onArtistClick(artist) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        val favorites = favoritesViewModel._demo.collectAsState()
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = if (artist.pic_url == "/assets/shared/missing_image.png") {
                    R.drawable.artsy_logo
                } else {
                    artist.pic_url
                },
                contentDescription = artist.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Star icon button in top right
            if(isLoggedIn) {
                IconButton(
                    onClick = { favoritesViewModel.toggleFavorite(artist.id ?: "") },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(26.dp)
                        .size(28.dp)
                        .background(Color(0xFFE2E8FF), shape = androidx.compose.foundation.shape.CircleShape)
                ) {
                    Icon(
                        imageVector = if (artist.id in favorites.value) Icons.Outlined.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favorite",
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .background(
                        color = Color(0xD3E2E8FF)
                    )
            ) {
                Text(
                    text = artist.title ?: "",
                    modifier = Modifier
                        .padding(12.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { onArtistClick(artist) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowForward,
                            contentDescription = "Go to details"
                        )
                    }
                }
            }
        }
    }
}