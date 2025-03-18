package com.androidfinalproject.hacktok.ui.resetPassword

data class ResetPasswordState(
    val email: String = "",
    val verificationCode: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val newPasswordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val newPasswordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val passwordRequirements: List<PasswordRequirement> = listOf(
        PasswordRequirement("At least 8 characters", false),
        PasswordRequirement("Contains uppercase letter", false),
        PasswordRequirement("Contains number", false),
        PasswordRequirement("Contains special character", false)
    )
) {
    val isFormValid: Boolean
        get() = newPassword.isNotBlank() &&
                confirmPassword.isNotBlank() &&
                newPassword == confirmPassword &&
                passwordRequirements.all { it.satisfied }
}

data class PasswordRequirement(
    val description: String,
    val satisfied: Boolean
)