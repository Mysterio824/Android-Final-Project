package com.androidfinalproject.hacktok.ui.forgotPassword

data class ForgotPasswordState(
    val email: String = "abc@gmail.com",
    val emailError: String? = null,
    val isCodeSent: Boolean = false,
    val isEmailEditable: Boolean = true,
    val verificationCode: String = "",
    val verificationCodeError: String? = null,
    val isLoading: Boolean = false,
    val isCodeVerified: Boolean = false
)