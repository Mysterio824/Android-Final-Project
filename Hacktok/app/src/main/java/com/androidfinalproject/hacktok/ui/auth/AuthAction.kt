package com.androidfinalproject.hacktok.ui.auth

sealed class AuthAction {
    data class UpdateEmail(val email: String) : AuthAction()
    data class UpdatePassword(val password: String) : AuthAction()
    data class UpdateConfirmPassword(val confirmPassword: String) : AuthAction()
    object ToggleAuthMode : AuthAction()
    object Submit : AuthAction()
    object GoogleSignIn : AuthAction()
    object ForgotPassword : AuthAction()
    data class SelectLanguage(val languageId : String) : AuthAction()
    object OnLoginSuccess : AuthAction()
}