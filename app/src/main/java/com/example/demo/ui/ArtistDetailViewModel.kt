package com.example.demo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.demo.models.Artwork
import com.example.demo.models.Category
import com.example.demo.models.Artist
import com.example.demo.ArtistRepository
import android.util.Log

class ArtistDetailViewModel : ViewModel() {
    private val repository = ArtistRepository()
    private val _artist = MutableStateFlow<Artist?>(null)
    val artist: StateFlow<Artist?> = _artist

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _artworks = MutableStateFlow<List<Artwork>?>(null)
    val artworks: StateFlow<List<Artwork>?> = _artworks

    // Categories state
    private val _categories = MutableStateFlow<List<Category>?>(null)
    val categories: StateFlow<List<Category>?> = _categories
    private val _isCategoriesLoading = MutableStateFlow(false)
    val isCategoriesLoading: StateFlow<Boolean> = _isCategoriesLoading

    // Similar artists state
    private val _similarArtists = MutableStateFlow<List<Artist>?>(null)
    val similarArtists: StateFlow<List<Artist>?> = _similarArtists

    fun loadArtist(artistId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _artist.value = repository.getArtistDetails(artistId)// TODO: Replace with your real API call
            Log.d("Debug", "artist: $artist")
            _isLoading.value = false
        }
    }

    fun loadArtworks(artistId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _artworks.value = repository.getArtworks(artistId) // TODO: Replace with your real API call
            Log.d("Debug", "artworks: $artworks")
            _isLoading.value = false
        }
    }

    fun loadCategories(artworkId: String) {
        viewModelScope.launch {
            _isCategoriesLoading.value = true
            _categories.value = repository.getCategories(artworkId) // TODO: Replace with your real API call
            Log.d("Debug", "categories: $categories")
            _isCategoriesLoading.value = false
        }
    }

    fun loadSimilarArtists(artistId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _similarArtists.value = repository.getSimilarArtists(artistId)// TODO: Replace with your real API call
            Log.d("Debug", "similarArtists: $similarArtists")
            _isLoading.value = false
        }
    }
} 