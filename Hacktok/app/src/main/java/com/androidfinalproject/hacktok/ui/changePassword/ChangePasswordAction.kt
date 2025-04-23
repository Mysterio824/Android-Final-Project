package com.androidfinalproject.hacktok.ui.changePassword

sealed class ChangePasswordAction {
    data class UpdateOldPassword(val password: String) : ChangePasswordAction()
    data class UpdateNewPassword(val password: String) : ChangePasswordAction()
    data class UpdateConfirmPassword(val password: String) : ChangePasswordAction()
    data object ToggleOldPasswordVisibility : ChangePasswordAction()
    data object ToggleNewPasswordVisibility : ChangePasswordAction()
    data object ToggleConfirmPasswordVisibility : ChangePasswordAction()
    data object ResetPassword : ChangePasswordAction()
    data object NavigateBack : ChangePasswordAction()
}