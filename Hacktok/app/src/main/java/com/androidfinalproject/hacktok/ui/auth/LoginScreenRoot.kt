package com.androidfinalproject.hacktok.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreenRoot(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onForgetPassword: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LoginScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is LoginAction.Submit -> {
                    if (state.emailError == null && state.passwordError == null) {
                        // You could set up navigation here
                    }
                }

                is LoginAction.ForgotPassword -> {
                    onForgetPassword()
                }

                is LoginAction.GoogleSignIn -> {

                }
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}