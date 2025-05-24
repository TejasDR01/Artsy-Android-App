//ArtistDetailScreen.kt
package com.example.demo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PersonSearch
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.InspectableModifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.demo.R
import com.example.demo.models.Artwork
import com.example.demo.models.Category
import com.example.demo.models.Artist
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.node.ModifierNodeElement
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.logging.Logger

// Placeholder data classes for demonstration
// Replace with your actual data models and ViewModel integration

@Composable
fun ArtistDetailScreen(
    artistId: String,
    onSimilarArtistClick: (Artist) -> Unit,
    viewModel: ArtistDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    favoritesViewModel: FavoritesViewModel,
    isLoggedIn: Boolean
) {
    val artist by viewModel.artist.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    // For dialog state
    var showCategoryDialog by remember { mutableStateOf(false) }
    var selectedArtworkId by remember { mutableStateOf<String?>(null) }

    val artworks by viewModel.artworks.collectAsState()
    // Observe categories and loading state from ViewModel
    val categories by viewModel.categories.collectAsState()
    val isCategoryLoading by viewModel.isCategoriesLoading.collectAsState()
    // Observe similar artists and loading state from ViewModel
    val similarArtists by viewModel.similarArtists.collectAsState()

    // Trigger the API call when artistId changes
    LaunchedEffect(selectedTab == 0) {
        viewModel.loadArtist(artistId)
    }
    // Load similar artists when Similar tab is selected
    LaunchedEffect(selectedTab == 1) {
        viewModel.loadArtworks(artistId)
    }
    // Load categories when dialog is shown
    LaunchedEffect(showCategoryDialog, selectedArtworkId) {
        if (showCategoryDialog && selectedArtworkId != null) {
            viewModel.loadCategories(selectedArtworkId!!)
        }
    }

    LaunchedEffect(selectedTab == 2) {
        viewModel.loadSimilarArtists(artistId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Tabs
        var tabTitles = listOf("Details", "Artworks")
        if(isLoggedIn) {
            tabTitles = listOf("Details", "Artworks", "Similar")
        }
        val tabIcons = listOf(Icons.Outlined.Info, Icons.Outlined.AccountBox, Icons.Outlined.PersonSearch)
        TabRow(
            selectedTabIndex = selectedTab,
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) },
                    icon = { Icon(tabIcons[index], contentDescription = title) }
                )
            }
        }
        // Progress bar while loading
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                CircularProgressIndicator()
            }
        } else {
            artist?.let { artist ->
                when (selectedTab) {
                    0 -> DetailsTab(artist)
                    1 -> ArtworksTab(
                        artworks = artworks ?: emptyList(),
                        onViewCategories = { categories, artworkId ->
                            showCategoryDialog = true
                            selectedArtworkId = artworkId
                        },
                        showCategoryDialog = showCategoryDialog,
                        onDismissDialog = { showCategoryDialog = false },
                        categories = categories,
                        isCategoryLoading = isCategoryLoading,
                    )
                    2 -> {
                        SimilarArtistsTab(
                            similarArtists ?: emptyList(),
                            onSimilarArtistClick = onSimilarArtistClick,
                            favoritesViewModel = favoritesViewModel,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailsTab(artist: Artist) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        artist.name?.let {
            Text(
                it,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        // Centered row for nationality, birthday - deathday
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            artist.nationality?.let {
                Text(it, fontSize = 18.sp, color = Color.Gray, textAlign = TextAlign.Center)
            }
            if (!artist.nationality.isNullOrBlank() && (!artist.birthday.isNullOrBlank() || !artist.deathday.isNullOrBlank())) {
                Text(", ", fontSize = 18.sp, color = Color.Gray)
            }
            if (!artist.birthday.isNullOrBlank() || !artist.deathday.isNullOrBlank()) {
                Text(
                    listOfNotNull(artist.birthday, artist.deathday?.let { "- $it" }).joinToString(" "),
                    fontSize = 18.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        artist.biography?.let {
            Text(
                it,
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Justify
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ArtworksTab(
    artworks: List<Artwork>,
    onViewCategories: (List<Category>, String) -> Unit,
    showCategoryDialog: Boolean,
    onDismissDialog: () -> Unit,
    categories: List<Category>?,
    isCategoryLoading: Boolean,
) {
    if (artworks.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE2E8FF)),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = "No Artworks",
                modifier = Modifier.padding(16.dp)
            )
        }
        return
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(artworks) { artwork ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = if (artwork.pic_url == "/assets/shared/missing_image.png") {
                            R.drawable.artsy_logo
                        } else {
                            artwork.pic_url
                        },
                        contentDescription = artwork.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                    Column(
                        modifier = Modifier.padding(12.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        artwork.title?.let {
                            Text(it, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            onViewCategories(
                                categories ?: emptyList(),
                                artwork.id
                            )
                        }) {
                            Text("View categories")
                        }
                    }
            }
        }
    }
    if (showCategoryDialog) {
        var currentCategoryIndex by remember { mutableStateOf(0) }

        AlertDialog(
            onDismissRequest = onDismissDialog,
            title = {
                Text(
                    "Categories",
                    fontWeight = FontWeight.Medium,
                    fontSize = 26.sp,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            text = {
                if (isCategoryLoading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    categories?.let { cats ->
                        if (cats.isEmpty()) {
                            Text("No categories available.")
                        } else {
                            // HIGHLIGHT: Added AnimatedContent for horizontal sliding transitions
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 18.dp)
                                    .height(400.dp)
                            ) {
                                // HIGHLIGHT: Using AnimatedContent for smooth transitions between categories
                                AnimatedContent(
                                    targetState = currentCategoryIndex,
                                    transitionSpec = {
                                        // HIGHLIGHT: Create a horizontal slide animation
                                        slideInHorizontally(initialOffsetX = { fullWidth ->
                                            if (targetState > initialState) fullWidth else -fullWidth
                                        }) with
                                                slideOutHorizontally(targetOffsetX = { fullWidth ->
                                                    if (targetState > initialState) -fullWidth else fullWidth
                                                })
                                    }
                                ) { index ->
                                    val category = cats[index]

                                    // Main category content - Card style
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(400.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        elevation = CardDefaults.cardElevation(4.dp),
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            // Category Image
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(120.dp)  // HIGHLIGHT: Reduced height to give more space for description
                                            ) {
                                                AsyncImage(
                                                    model = if (category.pic_url == "/assets/shared/missing_image.png") {
                                                        R.drawable.artsy_logo
                                                    } else {
                                                        category.pic_url
                                                    },
                                                    contentDescription = category.name,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }

                                            // Category Title
                                            Text(
                                                text = category.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 20.sp,
                                                modifier = Modifier.padding(top = 16.dp),
                                                textAlign = TextAlign.Center
                                            )

                                            // HIGHLIGHT: Added vertically scrollable container for description
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .weight(1f)
                                                    .padding(horizontal = 16.dp)
                                            ) {
                                                val scrollState = rememberScrollState()
                                                // Category Description (if available) - now scrollable
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .verticalScroll(scrollState)
                                                        .padding(vertical = 8.dp)
                                                ) {
                                                    category.description?.let {
                                                        Text(
                                                            text = it,
                                                            fontSize = 16.sp,
                                                            textAlign = TextAlign.Justify
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                // HIGHLIGHT: Enhanced navigation buttons with improved styling
                                // Left navigation button
                                IconButton(
                                    onClick = {
                                        if (currentCategoryIndex > 0) {
                                            currentCategoryIndex--
                                        }
                                    },
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .size(48.dp)
                                        .offset(x = -(42).dp)
                                        .background(Color.Transparent),
                                    enabled = currentCategoryIndex > 0
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowBack,
                                        contentDescription = "Previous",
                                        tint = if (currentCategoryIndex > 0) Color.DarkGray else Color.Transparent
                                    )
                                }

                                // Right navigation button
                                IconButton(
                                    onClick = {
                                        if (currentCategoryIndex < cats.size - 1) {
                                            currentCategoryIndex++
                                        }
                                    },
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .size(48.dp)
                                        .offset(x = (42).dp)
                                        .background(Color.Transparent),
                                    enabled = currentCategoryIndex < cats.size - 1
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowForward,
                                        contentDescription = "Next",
                                        tint = if (currentCategoryIndex < cats.size - 1) Color.DarkGray else Color.Transparent
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismissDialog,
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Close")
                    }
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun SimilarArtistsTab(
    similarArtists: List<Artist>,
    onSimilarArtistClick: (Artist) -> Unit,
    favoritesViewModel: FavoritesViewModel,
) {
    if (similarArtists.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE2E8FF)),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = "No Similar Artists",
                modifier = Modifier.padding(16.dp)
            )
        }
        return
    }
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(similarArtists) { artist ->
                SimilarArtistCard(
                    artist = artist,
                    onSimilarArtistClick = onSimilarArtistClick,
                    favoritesViewModel = favoritesViewModel
                )
            }
        }
    }
}

@Composable
fun SimilarArtistCard(
    artist: Artist,
    onSimilarArtistClick: (Artist) -> Unit,
    favoritesViewModel: FavoritesViewModel
) {
    val favorites = favoritesViewModel._demo.collectAsState()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(8.dp)
            .clickable { onSimilarArtistClick(artist) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = if (artist.pic_url == "/assets/shared/missing_image.png") {
                    R.drawable.artsy_logo
                } else {
                    artist.pic_url
                },
                contentDescription = artist.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Star icon button in top right
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .background(
                        color = Color(0xD3E2E8FF)
                    )
            ) {
                Text(
                    text = artist.name ?: "",
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
                        onClick = { onSimilarArtistClick(artist) },
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