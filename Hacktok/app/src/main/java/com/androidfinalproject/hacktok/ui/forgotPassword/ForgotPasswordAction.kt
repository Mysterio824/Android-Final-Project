package com.androidfinalproject.hacktok.ui.forgotPassword

sealed class ForgotPasswordAction {
    data class UpdateEmail(val email: String) : ForgotPasswordAction()
    data object SendCode : ForgotPasswordAction()
    data object ResendCode : ForgotPasswordAction()
    data object NavigateBack : ForgotPasswordAction()
}