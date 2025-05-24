// MainActivity.kt
package com.example.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.Popup
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.demo.ui.CommonTopBar
import com.example.demo.ui.TopBarState
import com.example.demo.ui.FavoritesScreen
import com.example.demo.ui.ArtistSearchScreen
import com.example.demo.ui.ArtistDetailScreen
import com.example.demo.ui.LoginScreen
import com.example.demo.ui.RegisterScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.demo.ui.FavoritesViewModel
import com.example.demo.ui.UserViewModel
import com.example.demo.ui.CustomSnackbar
import com.example.demo.ui.theme.DemoTheme


class MainActivity : ComponentActivity() {
    companion object {
        private lateinit var instance: MainActivity

        fun getInstance(): MainActivity {
            return instance
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        instance = this
        enableEdgeToEdge()

        // Initialize FavoritesViewModel at app startup
        val favoritesViewModel = FavoritesViewModel.getInstance()
        val userViewModel = UserViewModel.getInstance()

        setContent {
                val navController = rememberNavController()
                val searchViewModel: SearchViewModel = viewModel()
                val query by searchViewModel.query.collectAsState()
                val results by searchViewModel.results.collectAsState()
                var topBarState: TopBarState by remember { mutableStateOf<TopBarState>(TopBarState.Favorites) }
                var artistName by remember { mutableStateOf<String?>(null) }
                var previousArtistName by remember { mutableStateOf<String?>(null) }
                var currentArtistId by remember { mutableStateOf<String?>(null) }
                val isLoggedIn by userViewModel.isLoggedIn.collectAsState()

                // Load favorites when the app starts
                LaunchedEffect(Unit) {
                    if (isLoggedIn) {
                        favoritesViewModel.loadFavorites()
                    }
                }
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        CommonTopBar(
                            state = topBarState,
                            searchQuery = query,
                            onQueryChange = { searchViewModel.onQueryChanged(it) },
                            onSearchClick = {
                                topBarState = TopBarState.Search
                                navController.navigate("artistsearch")
                            },
                            onUserClick = {
                                topBarState = TopBarState.Login
                                navController.navigate("login")
                            },
                            onCloseSearch = {
                                topBarState = TopBarState.Favorites
                                searchViewModel.clearQuery()
                                navController.navigate("favorites")
                            },
                            onBackClick = {
                                if (topBarState is TopBarState.Detail) {
                                    previousArtistName?.let {
                                        artistName = it
                                        topBarState = TopBarState.Detail(it)
                                    }
                                }
                                navController.popBackStack()
                            },
                            currentArtistId = currentArtistId ?: "",
                            favoritesViewModel = favoritesViewModel,
                            userViewModel = userViewModel,
                            isLoggedIn = isLoggedIn
                        )
                        NavHost(
                            navController = navController,
                            startDestination = "favorites",
                            modifier = Modifier.weight(1f)
                        ) {
                            composable("login") {
                                topBarState = TopBarState.Login
                                LoginScreen(navController)
                            }

                            composable("register") {
                                topBarState = TopBarState.Register
                                RegisterScreen(navController)
                            }
                            composable("favorites") {
                                topBarState = TopBarState.Favorites
                                FavoritesScreen(
                                    onclicklogin = {
                                        topBarState = TopBarState.Login
                                        navController.navigate("login")
                                    },
                                    onArtistClick = {
                                        previousArtistName = null
                                        artistName = it.artistName
                                        currentArtistId = it.artistId
                                        topBarState = TopBarState.Detail(it.artistName)
                                        navController.navigate("artistdetail/${it.artistId}")
                                    },
                                    favoritesViewModel = favoritesViewModel,
                                    isLoggedIn = isLoggedIn
                                )
                            }
                            composable("artistsearch") {
                                topBarState = TopBarState.Search
                                ArtistSearchScreen(
                                    query = query,
                                    results = results,
                                    onArtistClick = { artist ->
                                        previousArtistName = null
                                        artistName = artist.title
                                        currentArtistId = artist.id
                                        topBarState = TopBarState.Detail(artist.title ?: "")
                                        navController.navigate("artistdetail/${artist.id}")
                                    },
                                    favoritesViewModel = favoritesViewModel,
                                    isLoggedIn = isLoggedIn
                                )
                            }
                            composable(
                                "artistdetail/{artistId}",
                                arguments = listOf(navArgument("artistId") {
                                    type = NavType.StringType
                                })
                            ) { backStackEntry ->
                                topBarState = TopBarState.Detail(artistName.toString())
                                val artistId = backStackEntry.arguments?.getString("artistId") ?: ""
                                currentArtistId = artistId
                                ArtistDetailScreen(
                                    artistId = artistId,
                                    onSimilarArtistClick = { artist ->
                                        previousArtistName = artistName
                                        artistName = artist.name
                                        currentArtistId = artist.id
                                        topBarState = TopBarState.Detail(artist.name ?: "")
                                        navController.navigate("artistdetail/${artist.id}")
                                    },
                                    favoritesViewModel = favoritesViewModel,
                                    isLoggedIn = isLoggedIn
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        CustomSnackbar()
                    }
                }
        }
    }
}