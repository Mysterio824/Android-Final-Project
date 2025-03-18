package com.androidfinalproject.hacktok.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.androidfinalproject.hacktok.ui.auth.LoginViewModel

@Composable
fun LoginScreenRoot(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LoginScreen(
        state = state,
        onAction = { action ->
            // Handle navigation or special actions here before passing to viewModel
            when (action) {
                is LoginAction.Submit -> {
                    // If you need to do something before passing to viewModel
                    // Example: if no errors, prepare for navigation
                    if (state.emailError == null && state.passwordError == null) {
                        // You could set up navigation here
                    }
                }
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}