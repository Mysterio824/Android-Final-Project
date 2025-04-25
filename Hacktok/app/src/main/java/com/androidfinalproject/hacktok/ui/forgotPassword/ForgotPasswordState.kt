package com.androidfinalproject.hacktok.ui.forgotPassword

data class ForgotPasswordState(
    val email: String = "abc@gmail.com",
    val emailError: String? = null,
    val isCodeSent: Boolean = false,
    val isEmailEditable: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null
)