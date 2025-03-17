package com.androidfinalproject.hacktok.ui.passRecovery

data class ForgotPasswordState(
    val email: String = "",
    val emailError: String? = null,
    val isCodeSent: Boolean = false,
    val isEmailEditable: Boolean = true,
    val verificationCode: String = "",
    val verificationCodeError: String? = null,
    val isLoading: Boolean = false
)