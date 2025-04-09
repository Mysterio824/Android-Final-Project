package com.androidfinalproject.hacktok.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class AuthViewModel : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val emailPattern = Pattern.compile(
        "[a-zA-Z0-9+._%\\-]{1,256}" +
                "@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.UpdateEmail -> updateEmail(action.email)
            is AuthAction.UpdatePassword -> updatePassword(action.password)
            is AuthAction.UpdateConfirmPassword -> updateConfirmPassword(action.confirmPassword)
            is AuthAction.ToggleAuthMode -> toggleAuthMode()
            is AuthAction.Submit -> submitForm()
            is AuthAction.GoogleSignIn -> signInWithGoogle()
            else -> {}
        }
    }

    private fun updateEmail(email: String) {
        _state.update { it.copy(email = email, emailError = validateEmail(email)) }
    }

    private fun updatePassword(password: String) {
        _state.update { it.copy(password = password, passwordError = validatePassword(password)) }
    }

    private fun updateConfirmPassword(confirmPassword: String) {
        _state.update { it.copy(confirmPassword = confirmPassword, confirmPasswordError = validateConfirmPassword(confirmPassword, _state.value.password)) }
    }

    private fun toggleAuthMode() {
        _state.update { it.copy(isLoginMode = !it.isLoginMode) }
    }

    private fun submitForm() {
        val currentState = _state.value

        val emailError = validateEmail(currentState.email)
        val passwordError = validatePassword(currentState.password)
        val confirmPasswordError = if (!currentState.isLoginMode) validateConfirmPassword(currentState.confirmPassword, currentState.password) else null

        _state.update {
            it.copy(
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError
            )
        }


        if (emailError == null && passwordError == null && (currentState.isLoginMode || confirmPasswordError == null)) {
            _state.update { it.copy(isLoading = true) }
            viewModelScope.launch {
                kotlinx.coroutines.delay(1000)
                _state.update { it.copy(isLoading = false) }
            }

            val isAdmin = currentState.email == "admin@gmail.com"
            _state.update {
                it.copy(
                    isLoginSuccess = true,
                    isAdmin = isAdmin
                )
            }
        }
    }

    private fun signInWithGoogle() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            _state.update { it.copy(isLoading = false) }
        }
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
