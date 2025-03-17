package com.androidfinalproject.hacktok.ui.auth

sealed class LoginAction {
    data class UpdateEmail(val email: String) : LoginAction()
    data class UpdatePassword(val password: String) : LoginAction()
    data class UpdateConfirmPassword(val confirmPassword: String) : LoginAction()
    object ToggleAuthMode : LoginAction()
    object Submit : LoginAction()
    object GoogleSignIn : LoginAction()
    object ForgotPassword : LoginAction()
}
