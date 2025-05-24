//UserViewModel.kt
package com.example.demo.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demo.UserRepository
import com.example.demo.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val repository = UserRepository()

    // User login state
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    // Current user data
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    // Get the snackbar manager instance
    private val snackbarManager = SnackbarManager.getInstance()

    companion object {
        @Volatile
        private var INSTANCE: UserViewModel? = null

        fun getInstance(): UserViewModel {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserViewModel().also { INSTANCE = it }
            }
        }
    }
    init {
        // Check for stored user data when ViewModel is created
        checkActiveSession()
    }

    fun logout() {
        viewModelScope.launch {
            try {
                repository.logout()
                clearUserState()
                snackbarManager.showMessage("Logged out successfully", true)

            } catch (e: Exception) {
                Log.e("UserViewModel", "Logout error: ${e.message}")
                // Still reset the user state even if the API call fails
                clearUserState()
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            try {
                    if(repository.delete()) {
                        clearUserState()
                    }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Logout error: ${e.message}")
                // Still reset the user state even if the API call fails
                clearUserState()
            }
        }
    }

    fun checkActiveSession() {
        viewModelScope.launch {
            try {
                // Get user from local storage instead of API
                val user = repository.getUserFromLocalStorage()

                if (user != null) {
                    Log.d("Debug", "Found active user session for: ${user}")
                    _currentUser.value = user
                    _isLoggedIn.value = true
                } else {
                    Log.d("Debug", "No active user session found")
                }
            } catch (e: Exception) {
                Log.e("Debug", "Session check error: ${e.message}")
            }
        }
    }

    fun setUser(user : User) {
        _currentUser.value = user
        _isLoggedIn.value = true
    }

    fun clearUserState() {
        _currentUser.value = null
        _isLoggedIn.value = false
    }
}