package com.androidfinalproject.hacktok.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AuthScreenRoot(
    viewModel: AuthViewModel = viewModel(),
    onLoginSuccess: (Boolean) -> Unit,
    onForgetPassword: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AuthScreen(
        state = state,
        onAction = { action -> viewModel.onAction(action)
        }
    )
}