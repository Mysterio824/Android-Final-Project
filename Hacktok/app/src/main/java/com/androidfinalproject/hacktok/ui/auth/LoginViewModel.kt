package com.androidfinalproject.hacktok.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    // Email validation pattern
    private val emailPattern = Pattern.compile(
        "[a-zA-Z0-9+._%\\-]{1,256}" +
                "@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.UpdateEmail -> updateEmail(action.email)
            is LoginAction.UpdatePassword -> updatePassword(action.password)
            is LoginAction.UpdateConfirmPassword -> updateConfirmPassword(action.confirmPassword)
            is LoginAction.ToggleAuthMode -> toggleAuthMode()
            is LoginAction.Submit -> submitForm()
            is LoginAction.GoogleSignIn -> signInWithGoogle()
            is LoginAction.ForgotPassword -> handleForgotPassword()
        }
    }

    private fun updateEmail(email: String) {
        _state.update { currentState ->
            currentState.copy(
                email = email,
                emailError = validateEmail(email)
            )
        }
    }

    private fun updatePassword(password: String) {
        _state.update { currentState ->
            currentState.copy(
                password = password,
                passwordError = validatePassword(password)
            )
        }
    }

    private fun updateConfirmPassword(confirmPassword: String) {
        _state.update { currentState ->
            currentState.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = validateConfirmPassword(confirmPassword, currentState.password)
            )
        }
    }

    private fun toggleAuthMode() {
        _state.update { it.copy(isLoginMode = !it.isLoginMode) }
    }

    private fun submitForm() {
        val currentState = _state.value

        // Validate all fields
        val emailError = validateEmail(currentState.email)
        val passwordError = validatePassword(currentState.password)
        val confirmPasswordError = if (!currentState.isLoginMode) {
            validateConfirmPassword(currentState.confirmPassword, currentState.password)
        } else null

        // Update UI state with any validation errors
        _state.update { it.copy(
            emailError = emailError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError
        )}

        // Check if there are any validation errors
        if (emailError == null && passwordError == null &&
            (currentState.isLoginMode || confirmPasswordError == null)) {
            // Proceed with authentication
            _state.update { it.copy(isLoading = true) }

            viewModelScope.launch {
                // Here you would normally call your repository to authenticate
                // For demo purposes we'll just simulate a delay
                kotlinx.coroutines.delay(1000)

                if (currentState.isLoginMode) {
                    // Perform login
                    // authRepository.login(currentState.email, currentState.password)
                } else {
                    // Perform registration
                    // authRepository.register(currentState.email, currentState.password)
                }

                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun signInWithGoogle() {
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            // Here you would normally call your repository to authenticate with Google
            // For demo purposes we'll just simulate a delay
            kotlinx.coroutines.delay(1000)

            // authRepository.signInWithGoogle()

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun handleForgotPassword() {
        // Handle forgot password logic
        // Perhaps navigate to a forgot password screen or show a dialog
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isEmpty() -> "Email cannot be empty"
            !emailPattern.matcher(email).matches() -> "Please enter a valid email address"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isEmpty() -> "Password cannot be empty"
            password.length < 8 -> "Password must be at least 8 characters"
            !password.any { it.isDigit() } -> "Password must contain at least one number"
            !password.any { it.isUpperCase() } -> "Password must contain at least one uppercase letter"
            else -> null
        }
    }

    private fun validateConfirmPassword(confirmPassword: String, password: String): String? {
        return when {
            confirmPassword.isEmpty() -> "Please confirm your password"
            confirmPassword != password -> "Passwords do not match"
            else -> null
        }
    }
}
