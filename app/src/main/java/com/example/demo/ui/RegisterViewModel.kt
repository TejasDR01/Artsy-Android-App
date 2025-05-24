// RegisterViewModel.kt
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

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()
    private val snackbarManager = SnackbarManager.getInstance()
    private val userviewmodel = UserViewModel.getInstance()
    private val repository = UserRepository()

    fun register(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                // Set loading state
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                val result : User? = repository.register(fullName, email, password)
                if (result != null) {
                    userviewmodel.setUser(result)
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    snackbarManager.showMessage("Registration successful", true)
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Registration failed. Please try again."
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
        _uiState.update { RegisterUiState() }
    }
}

data class RegisterUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)