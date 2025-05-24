//CommonTopBar.kt
package com.example.demo.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CommonTopBar(
    modifier: Modifier = Modifier,
    state: TopBarState,
    onSearchClick: () -> Unit = {},
    onUserClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    searchQuery: String = "",
    onQueryChange: (String) -> Unit = {},
    onCloseSearch: () -> Unit = {},
    currentArtistId: String,
    favoritesViewModel: FavoritesViewModel,
    userViewModel: UserViewModel,
    isLoggedIn: Boolean
) {
    val currentUser = userViewModel.currentUser.collectAsState()
    var showDropdown by remember { mutableStateOf<Boolean>(false) }
    Surface(
        color = Color(0xFFE2E8FF),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .statusBarsPadding() // This ensures content doesn't overlap with status bar
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
        when (state) {
            is TopBarState.Login -> {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    title = {
                        Text(
                            text = "Login",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }

            is TopBarState.Register -> {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    title = {
                        Text(
                            text = "Register",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }

            is TopBarState.Favorites -> {
                TopAppBar(
                    title = {
                        Text(
                            "Artist Search",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    actions = {
                        IconButton(onClick = onSearchClick) {
                            Icon(Icons.Filled.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = {
                            if (isLoggedIn) {
                                showDropdown = !showDropdown
                            } else {
                                onUserClick()
                            }
                        }) {
                            if (isLoggedIn && currentUser.value?.profileImageUrl != null) {
                                AsyncImage(
                                    model = currentUser.value?.profileImageUrl,
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(Icons.Outlined.Person, contentDescription = "Profile")
                            }

                        }
                        DropdownMenu(
                            expanded = showDropdown,
                            onDismissRequest = { showDropdown = false },
                            modifier = Modifier
                                .width(140.dp)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Log out", color = Color.Blue) },
                                onClick = {
                                    userViewModel.logout()
                                    showDropdown = false
                                }
                            )
                            Divider()
                            DropdownMenuItem(
                                text = { Text("Delete account", color = Color.Red) },
                                onClick = {
                                    userViewModel.delete()
                                    showDropdown = false
                                }
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }

            is TopBarState.Search -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onQueryChange,
                        modifier = Modifier
                            .weight(2f)
                            .padding(start = 8.dp),
                        placeholder = { Text("Search artists...", fontSize = 20.sp) },
                        textStyle = LocalTextStyle.current.copy(fontSize = 20.sp),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    IconButton(onClick = onCloseSearch) {
                        Icon(Icons.Filled.Close, contentDescription = "Close Search")
                    }
                }
            }

            is TopBarState.Detail -> {
                val favorites = favoritesViewModel._demo.collectAsState()
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    title = {
                        Text(
                            text = state.artistName,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    },
                    actions = {
                        if (isLoggedIn) {
                            IconButton(onClick = {
                                currentArtistId?.let { id ->
                                    favoritesViewModel.toggleFavorite(id)
                                }
                            }) {
                                Icon(
                                    imageVector = if (currentArtistId in favorites.value) Icons.Outlined.Star else Icons.Outlined.StarBorder,
                                    contentDescription = "",
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFE3E7F7)
                    )
                )
            }
        }
        }
    }
}

sealed class TopBarState {
    object Login : TopBarState()
    object Register : TopBarState()
    object Favorites : TopBarState()
    object Search : TopBarState()
    data class Detail(val artistName: String) : TopBarState()
}