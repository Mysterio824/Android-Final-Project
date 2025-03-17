package com.androidfinalproject.hacktok.ui.auth.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.androidfinalproject.hacktok.ui.auth.LoginState

@Composable
fun LoginHeader(state: LoginState) {
    Text(
        text = if (state.isLoginMode) "Welcome Back" else "Create Account",
        style = MaterialTheme.typography.headlineMedium
    )
    Text(
        text = if (state.isLoginMode) "Sign in to continue" else "Please fill in your details",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}