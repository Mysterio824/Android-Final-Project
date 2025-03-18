package com.androidfinalproject.hacktok.ui.passRecovery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ForgotPasswordScreenRoot(
    viewModel: ForgotPasswordViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onResetSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ForgotPasswordScreen(
        state = state,
        onAction = { action ->
            // Handle navigation or special actions here before passing to viewModel
            when (action) {
                is ForgotPasswordAction.VerifyCode -> {
                    // If code verification is successful, we might want to navigate
                    // For now, we'll just pass the action to the viewModel
                }
                else -> Unit
            }
            viewModel.onAction(action)
        },
        onNavigateBack = onNavigateBack
    )
}