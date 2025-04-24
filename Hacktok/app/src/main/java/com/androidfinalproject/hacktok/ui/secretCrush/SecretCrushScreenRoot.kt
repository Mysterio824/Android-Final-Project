package com.androidfinalproject.hacktok.ui.secretCrush

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.androidfinalproject.hacktok.ui.secretcrush.SecretCrushScreen

@Composable
fun SecretCrushScreenRoot(
    viewModel: SecretCrushViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    SecretCrushScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is SecretCrushAction.NavigateBack -> onNavigateBack()
                else -> viewModel.onAction(action)
            }
        }
    )
}