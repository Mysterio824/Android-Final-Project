package com.androidfinalproject.hacktok.ui.changePassword

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ChangePasswordScreenRoot(
    viewModel: ChangePasswordViewModel = hiltViewModel(),
    onResetSuccess: () -> Unit,
    onGoBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel.isResetSuccessful()) {
        if (viewModel.isResetSuccessful()) {
            onResetSuccess()
        }
    }

    ChangePasswordScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is ChangePasswordAction.NavigateBack -> onGoBack()
                else -> viewModel.onAction(action)
            }
        }
    )
}