package com.androidfinalproject.hacktok.ui.forgotPassword

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay

@Composable
fun ForgotPasswordScreenRoot(
    viewModel: ForgotPasswordViewModel = viewModel(),
    onGoBack: () -> Unit,
    onResetSuccess: (String, String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isCodeVerified) {
        if(state.isCodeVerified) {
            onResetSuccess(state.email, state.verificationCode)
        }
    }

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