package com.androidfinalproject.hacktok.ui.auth.validation

import java.util.regex.Pattern

class AuthValidator {
    private val emailPattern = Pattern.compile(
        "[a-zA-Z0-9+._%\\-]{1,256}" +
                "@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    fun validateEmail(email: String): String? {
        return when {
            email.isEmpty() -> "Email cannot be empty"
            !emailPattern.matcher(email).matches() -> "Please enter a valid email address"
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.isEmpty() -> "Password cannot be empty"
            password.length < 8 -> "Password must be at least 8 characters"
            !password.any { it.isDigit() } -> "Password must contain at least one number"
            !password.any { it.isUpperCase() } -> "Password must contain at least one uppercase letter"
            else -> null
        }
    }

    fun validateConfirmPassword(confirmPassword: String, password: String): String? {
        return when {
            confirmPassword.isEmpty() -> "Please confirm your password"
            confirmPassword != password -> "Passwords do not match"
            else -> null
        }
    }
} 