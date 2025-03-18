package com.androidfinalproject.hacktok.ui.passRecovery

sealed class ForgotPasswordAction {
    data class UpdateEmail(val email: String) : ForgotPasswordAction()
    data class UpdateVerificationCode(val code: String) : ForgotPasswordAction()
    data object SendCode : ForgotPasswordAction()
    data object ResendCode : ForgotPasswordAction()
    data object VerifyCode : ForgotPasswordAction()
}