//FavoritesScreen.kt
package com.example.demo.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import com.example.demo.models.Favorite
import androidx.compose.ui.draw.clip

@Composable
fun FavoritesScreen(
    onclicklogin: () -> Unit,
    onArtistClick: (Favorite) -> Unit,
    favoritesViewModel: FavoritesViewModel,
    isLoggedIn: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Date
        Text(
            text = "8 May 2025",
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            fontSize = 16.sp,
            color = Color(0xFF888888)
        )
        // Favorites Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Favorites",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFEE8E8E8))
                    .padding(5.dp)
                    .align(Alignment.CenterHorizontally)
            )
        if(isLoggedIn) {
            val favorites = favoritesViewModel.favorites.collectAsState()
            when {
                favorites.value.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFE2E8FF)),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = "No Favorites",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                else -> {
                    FavoritesList(
                        favorites = favorites.value,
                        onArtistClick = onArtistClick
                    )
                }
            }
        }
        else {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { onclicklogin() },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D588C))
            ) {
                Text(
                    "Log in to see favorites",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
            val context = LocalContext.current
            Text(
                text = "Powered by Artsy",
                fontStyle = FontStyle.Italic,
                color = Color(0xFF6B6B6B),
                modifier = Modifier
                    .padding(top = 32.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://artsy.net"))
                        context.startActivity(intent)
                    }
            )
        }
    }
}

@Composable
fun FavoritesList(
    favorites: List<Favorite>,
    onArtistClick: (Favorite) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(favorites) { favorite ->
            FavoriteItem(
                favorite = favorite,
                onClick = { onArtistClick(favorite) }
            )
        }
    }
}

@Composable
fun FavoriteItem(
    favorite: Favorite,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = favorite.artistName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Row {
                if (favorite.nationality != null) {
                    Text(
                        text = favorite.nationality,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                if (favorite.birthday != null) {
                    val lifespan = if (favorite.deathday != null) {
                        ", ${favorite.birthday} - ${favorite.deathday}"
                    } else {
                        ", ${favorite.birthday}"
                    }

                    Text(
                        text = if (favorite.nationality != null && favorite.nationality.isNotEmpty()) ", $lifespan" else lifespan,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            RealTimeTimeAgo(
                timestamp = favorite.addedAt,
                fontSize = 14
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "View artist",
                tint = Color.Gray
            )
        }
    }
}