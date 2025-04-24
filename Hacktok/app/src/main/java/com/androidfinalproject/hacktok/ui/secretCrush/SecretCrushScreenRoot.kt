package com.androidfinalproject.hacktok.ui.secretcrush

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SecretCrushScreenRoot(
    viewModel: SecretCrushViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToSelectCrush: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    SecretCrushScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is SecretCrushAction.NavigateBack -> onNavigateBack()
                is SecretCrushAction.NavigateToSelectCrush -> onNavigateToSelectCrush()
                else -> viewModel.onAction(action)
            }
        }
    )
}