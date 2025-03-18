package com.androidfinalproject.hacktok.ui.resetPassword

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun ResetPasswordScreenRoot(
    viewModel: ResetPasswordViewModel,
    email: String,
    verificationCode: String,
    onResetSuccess: () -> Unit,
    onGoBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setEmailAndCode(email, verificationCode)
    }

    LaunchedEffect(viewModel.isResetSuccessful()) {
        if (viewModel.isResetSuccessful()) {
            onResetSuccess()
        }
    }

    ResetPasswordScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is ResetPasswordAction.NavigateBack -> onGoBack()
                else -> viewModel.onAction(action)
            }
        }
    )
}