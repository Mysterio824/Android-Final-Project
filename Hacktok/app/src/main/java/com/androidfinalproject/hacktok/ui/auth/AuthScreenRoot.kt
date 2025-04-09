package com.androidfinalproject.hacktok.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

    LaunchedEffect(state.isLoginSuccess) {
        if (state.isLoginSuccess) {
            onLoginSuccess(state.isAdmin)
        }
    }


    AuthScreen(
        state = state,
        onAction = { action ->
            when(action) {
                is AuthAction.ForgotPassword -> onForgetPassword()

                else -> viewModel.onAction(action)
            }
        }
    )
}