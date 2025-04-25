package com.androidfinalproject.hacktok.ui.forgotPassword

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ForgotPasswordScreenRoot(
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
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