// LoginVeiwModel.kt
package com.example.demo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demo.models.User
import com.example.demo.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    private val snackbarManager = SnackbarManager.getInstance()
    private val userviewmodel = UserViewModel.getInstance()
    private val repository = UserRepository()
    private val favoritesViewModel = FavoritesViewModel.getInstance()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                // Set loading state
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                // Simulate API call
                val result : User? = repository.login(email, password)
                if (result != null) {
                    userviewmodel.setUser(result)
                    favoritesViewModel.loadFavorites()
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    snackbarManager.showMessage("Login successful", true)
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Username or password is incorrect"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "An unexpected error occurred"
                    )
                }
            }
        }
    }

    fun resetState() {
        _uiState.update { LoginUiState() }
    }
}

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

