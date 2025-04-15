package com.androidfinalproject.hacktok.ui.auth

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoginMode: Boolean = true,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val language: String = "English (US)",
    val isLoginSuccess: Boolean = false,
    val isAdmin: Boolean = false
)