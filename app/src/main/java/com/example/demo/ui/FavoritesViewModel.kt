//FavoriteViewModel.kt
package com.example.demo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.demo.models.Favorite
import android.util.Log
import com.example.demo.UserRepository

class FavoritesViewModel : ViewModel() {
    private val repository = UserRepository()
    private val _favorites = MutableStateFlow<List<Favorite>>(emptyList())
    val favorites: StateFlow<List<Favorite>> = _favorites
    private val demo = MutableStateFlow<Set<String>>(emptySet())
    val _demo: StateFlow<Set<String>> = demo
    private val snackbarManager = SnackbarManager.getInstance()

    companion object {
        @Volatile
        private var INSTANCE: FavoritesViewModel? = null

        fun getInstance(): FavoritesViewModel {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FavoritesViewModel().also { INSTANCE = it }
            }
        }
    }

    fun toggleFavorite(artistId: String) {
        viewModelScope.launch {
            //val existingFavorite = _favorites.value.find { it.artistId == artistId }
            Log.e("DEBUG", "hi : ${artistId}")
            if (artistId in demo.value) {
                if(repository.removefavorite(artistId)) {
                    _favorites.value = _favorites.value.filter { it.artistId != artistId }
                    demo.value = demo.value - artistId
                    snackbarManager.showMessage("Removed from favorites",true)
                }
            } else {
                val result = repository.addfavorite(artistId)
                if(result != null) {
                    _favorites.value = listOf(result) + _favorites.value
                    demo.value = demo.value + artistId
                    snackbarManager.showMessage("Added to favorites",true)
                }
            }
        }
        Log.e("DEBUG", "demo : ${demo.value}")
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _favorites.value = repository.getfavorites()
            demo.value = _favorites.value.map { it.artistId }.toSet()
        }
    }
} 