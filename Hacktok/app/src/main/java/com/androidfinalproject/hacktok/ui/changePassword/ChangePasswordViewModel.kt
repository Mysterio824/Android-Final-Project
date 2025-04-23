package com.androidfinalproject.hacktok.ui.changePassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.service.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {
    private val _state = MutableStateFlow(ChangePasswordState())
    val state: StateFlow<ChangePasswordState> = _state.asStateFlow()

    private var resetSuccess = false

    fun onAction(action: ChangePasswordAction) {
        when (action) {
            is ChangePasswordAction.UpdateNewPassword -> updateNewPassword(action.password)
            is ChangePasswordAction.UpdateConfirmPassword -> updateConfirmPassword(action.password)
            is ChangePasswordAction.UpdateOldPassword -> updateOldPassword(action.password)
            is ChangePasswordAction.ToggleOldPasswordVisibility -> toggleOldPasswordVisibility()
            is ChangePasswordAction.ToggleNewPasswordVisibility -> toggleNewPasswordVisibility()
            is ChangePasswordAction.ToggleConfirmPasswordVisibility -> toggleConfirmPasswordVisibility()
            is ChangePasswordAction.ResetPassword -> resetPassword()
            else -> {}
        }
    }

    private fun updateOldPassword(password: String) {
        _state.update {
            val passwordRequirements = validatePasswordRequirements(password)
            it.copy(
                oldPassword = password,
                passwordRequirements = passwordRequirements,
                oldPasswordError = if (password.isBlank()) "Password cannot be empty" else null
            )
        }
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

    private fun toggleOldPasswordVisibility() {
        _state.update { it.copy(oldPasswordVisible = !it.oldPasswordVisible) }
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
                    oldPasswordError = if (it.oldPassword.isBlank()) "Old password can't be empty" else null,
                    newPasswordError = if (it.newPassword.isBlank()) "Password cannot be empty" else null,
                    confirmPasswordError = when {
                        it.confirmPassword.isBlank() -> "Confirm password can't be empty"
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
                val res = authService.changePassword(currentState.oldPassword, currentState.newPassword)

                if(res == "Password changed successfully"){
                    resetSuccess = true
                    _state.update { it.copy(isLoading = false) }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = res
                        )
                    }
                }
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