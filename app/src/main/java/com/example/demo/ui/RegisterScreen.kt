// RegisterScreen.kt
package com.example.demo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var fullNameFieldTouched by remember { mutableStateOf(false) }
    var emailFieldTouched by remember { mutableStateOf(false) }
    var passwordFieldTouched by remember { mutableStateOf(false) }

    // Register Form
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Full Name Field
        OutlinedTextField(
            value = fullName,
            onValueChange = {
                fullName = it
                if (fullNameFieldTouched) {
                    validateFullName(it)?.let { error ->
                        fullNameError = error
                    } ?: run { fullNameError = null }
                }
            },
            label = { Text("Full Name") },
            placeholder = { Text("Enter full name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (fullNameError != null) 0.dp else 16.dp)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        fullNameFieldTouched = true
                    } else if (fullNameFieldTouched) {
                        // Validate when focus is lost and field was touched
                        validateFullName(fullName)?.let { error ->
                            fullNameError = error
                        } ?: run { fullNameError = null }
                    }
                },
            singleLine = true,
            isError = fullNameError != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF3A5BB6),
                unfocusedBorderColor = Color.Gray,
                errorBorderColor = Color.Red
            )
        )

        if (fullNameError != null) {
            Text(
                text = fullNameError!!,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, bottom = 16.dp, top = 2.dp)
                    .align(Alignment.Start)
            )
        }

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                if (emailFieldTouched) {
                    validateEmail(it)?.let { error ->
                        emailError = error
                    } ?: run { emailError = null }
                }
            },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (emailError != null) 0.dp else 16.dp)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        emailFieldTouched = true
                    } else if (emailFieldTouched) {
                        // Validate when focus is lost and field was touched
                        validateEmail(email)?.let { error ->
                            emailError = error
                        } ?: run { emailError = null }
                    }
                },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = emailError != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF3A5BB6),
                unfocusedBorderColor = Color.Gray,
                errorBorderColor = Color.Red
            )
        )

        if (emailError != null) {
            Text(
                text = emailError!!,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, bottom = 16.dp, top = 2.dp)
                    .align(Alignment.Start)
            )
        }

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (passwordFieldTouched) {
                    validatePassword(it)?.let { error ->
                        passwordError = error
                    } ?: run { passwordError = null }
                }
            },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (passwordError != null) 0.dp else 24.dp)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        passwordFieldTouched = true
                    } else if (passwordFieldTouched) {
                        // Validate when focus is lost and field was touched
                        validatePassword(password)?.let { error ->
                            passwordError = error
                        } ?: run { passwordError = null }
                    }
                },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = passwordError != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF3A5BB6),
                unfocusedBorderColor = Color.Gray,
                errorBorderColor = Color.Red
            )
        )

        if (passwordError != null) {
            Text(
                text = passwordError!!,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, bottom = 24.dp, top = 2.dp)
                    .align(Alignment.Start)
            )
        }

        // Register Button
        Button(
            onClick = {
                // Set all fields as touched to trigger validation
                fullNameFieldTouched = true
                emailFieldTouched = true
                passwordFieldTouched = true

                // Validate inputs
                val fullNameValidationError = validateFullName(fullName)
                val emailValidationError = validateEmail(email)
                val passwordValidationError = validatePassword(password)

                fullNameError = fullNameValidationError
                emailError = emailValidationError
                passwordError = passwordValidationError

                // Only proceed if there are no validation errors
                if (fullNameValidationError == null && emailValidationError == null && passwordValidationError == null) {
                    viewModel.register(fullName, email, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3A5BB6),
                disabledContainerColor = Color(0xFF3A5BB6).copy(alpha = 0.5f)
            ),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Register", fontSize = 16.sp)
            }
        }

        // Error message
        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage!!,
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }

        // Login Link
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Already have an account? ",
                color = Color.Gray
            )
            Text(
                text = "Login",
                color = Color(0xFF3A5BB6),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { navController.navigate("login") }
            )
        }
    }

    // Show success snackbar if registration successful
    if (uiState.isSuccess) {
        viewModel.resetState()
        navController.navigate("favorites") {
            popUpTo("login") { inclusive = true }
        }
    }
}

// Helper functions for validation
private fun validateFullName(fullName: String): String? {
    return when {
        fullName.isBlank() -> "Full name cannot be empty"
        else -> null
    }
}

private fun validateEmail(email: String): String? {
    return when {
        email.isBlank() -> "Email cannot be empty"
        !isValidEmail(email) -> "Invalid email format"
        else -> null
    }
}

private fun validatePassword(password: String): String? {
    return when {
        password.isBlank() -> "Password cannot be empty"
        else -> null
    }
}

private fun isValidEmail(email: String): Boolean {
    val emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
    return email.matches(emailRegex)
}