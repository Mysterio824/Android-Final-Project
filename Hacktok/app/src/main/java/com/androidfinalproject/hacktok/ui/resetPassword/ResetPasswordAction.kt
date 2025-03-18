package com.androidfinalproject.hacktok.ui.resetPassword

sealed class ResetPasswordAction {
    data class UpdateNewPassword(val password: String) : ResetPasswordAction()
    data class UpdateConfirmPassword(val password: String) : ResetPasswordAction()
    data object ToggleNewPasswordVisibility : ResetPasswordAction()
    data object ToggleConfirmPasswordVisibility : ResetPasswordAction()
    data object ResetPassword : ResetPasswordAction()
    data object NavigateBack : ResetPasswordAction()
}