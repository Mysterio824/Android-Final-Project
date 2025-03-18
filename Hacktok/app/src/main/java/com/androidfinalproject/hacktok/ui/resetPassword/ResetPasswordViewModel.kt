package com.androidfinalproject.hacktok.ui.resetPassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResetPasswordViewModel : ViewModel() {
    private val _state = MutableStateFlow(ResetPasswordState())
    val state: StateFlow<ResetPasswordState> = _state.asStateFlow()

    private var resetSuccess = false

    fun onAction(action: ResetPasswordAction) {
        when (action) {
            is ResetPasswordAction.UpdateNewPassword -> updateNewPassword(action.password)
            is ResetPasswordAction.UpdateConfirmPassword -> updateConfirmPassword(action.password)
            is ResetPasswordAction.ToggleNewPasswordVisibility -> toggleNewPasswordVisibility()
            is ResetPasswordAction.ToggleConfirmPasswordVisibility -> toggleConfirmPasswordVisibility()
            is ResetPasswordAction.ResetPassword -> resetPassword()
            is ResetPasswordAction.NavigateBack -> {}
        }
    }

    fun setEmailAndCode(email: String, code: String) {
        _state.update { it.copy(email = email, verificationCode = code) }
    }

    private fun updateNewPassword(password: String) {
        _state.update {
            val passwordRequirements = validatePasswordRequirements(password)
            it.copy(
                newPassword = password,
                passwordRequirements = passwordRequirements,
                newPasswordError = if (password.isBlank()) "Password cannot be empty" else null
            )
        }
    }

    private fun updateConfirmPassword(password: String) {
        _state.update {
            val passwordsMatch = password == it.newPassword
            it.copy(
                confirmPassword = password,
                confirmPasswordError = when {
                    password.isBlank() -> "Confirm password cannot be empty"
                    !passwordsMatch -> "Passwords do not match"
                    else -> null
                }
            )
        }
    }

    private fun toggleNewPasswordVisibility() {
        _state.update { it.copy(newPasswordVisible = !it.newPasswordVisible) }
    }

    private fun toggleConfirmPasswordVisibility() {
        _state.update { it.copy(confirmPasswordVisible = !it.confirmPasswordVisible) }
    }

    private fun validatePasswordRequirements(password: String): List<PasswordRequirement> {
        return listOf(
            PasswordRequirement("At least 8 characters", password.length >= 8),
            PasswordRequirement("Contains uppercase letter", password.any { it.isUpperCase() }),
            PasswordRequirement("Contains number", password.any { it.isDigit() }),
            PasswordRequirement("Contains special character", password.any { !it.isLetterOrDigit() })
        )
    }

    private fun resetPassword() {
        val currentState = _state.value

        if (!currentState.isFormValid) {
            _state.update {
                it.copy(
                    newPasswordError = if (it.newPassword.isBlank()) "Password cannot be empty" else null,
                    confirmPasswordError = when {
                        it.confirmPassword.isBlank() -> "Confirm password cannot be empty"
                        it.newPassword != it.confirmPassword -> "Passwords do not match"
                        else -> null
                    }
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // In a real app, you would make an API call here
                kotlinx.coroutines.delay(1500)

                resetSuccess = true
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to reset password"
                    )
                }
            }
        }
    }

    fun isResetSuccessful(): Boolean = resetSuccess
}