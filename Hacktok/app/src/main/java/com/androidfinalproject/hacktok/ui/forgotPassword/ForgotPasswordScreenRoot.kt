package com.androidfinalproject.hacktok.ui.forgotPassword

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ForgotPasswordScreenRoot(
    viewModel: ForgotPasswordViewModel = viewModel(),
    onGoBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ForgotPasswordScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is ForgotPasswordAction.NavigateBack -> onGoBack()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}