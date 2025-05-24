package com.example.demo.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Singleton class to manage showing Snackbars from anywhere in the app
 */
class SnackbarManager private constructor() {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _isVisible = MutableStateFlow(false)
    val isVisible: StateFlow<Boolean> = _isVisible

    private val _isSuccess = MutableStateFlow(true)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    companion object {
        @Volatile
        private var INSTANCE: SnackbarManager? = null

        fun getInstance(): SnackbarManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SnackbarManager().also { INSTANCE = it }
            }
        }
    }

    fun showMessage(message: String, isSuccess: Boolean = true) {
        _message.value = message
        _isSuccess.value = isSuccess
        _isVisible.value = true

        // Auto-dismiss after 2 seconds
        dismissAfterDelay()
    }

    fun dismiss() {
        _isVisible.value = false
        _message.value = null
    }

    private fun dismissAfterDelay(delayMillis: Long = 2000) {
        coroutineScope.launch {
            delay(delayMillis)
            dismiss()
        }
    }
}