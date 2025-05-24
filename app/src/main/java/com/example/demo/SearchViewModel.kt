package com.example.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.demo.models.Artist

class SearchViewModel : ViewModel() {
    private val repository = ArtistRepository()
    private var searchJob: Job? = null

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _results = MutableStateFlow<List<Artist>>(emptyList())
    val results: StateFlow<List<Artist>> = _results

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
        if (newQuery.length >= 3) {
            searchJob?.cancel()
            searchJob = viewModelScope.launch {
                val artists = repository.searchArtists(newQuery)
                _results.value = artists
            }
        } else {
            _results.value = emptyList()
        }
    }

    fun clearQuery() {
        _query.value = ""
        _results.value = emptyList()
    }
} 