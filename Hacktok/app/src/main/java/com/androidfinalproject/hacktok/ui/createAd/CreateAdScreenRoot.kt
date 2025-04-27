package com.androidfinalproject.hacktok.ui.createAd

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CreateAdScreenRoot(
    viewModel: CreateAdViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    CreateAdScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is CreateAdAction.NavigateBack -> onNavigateBack()
                else -> viewModel.onAction(action)
            }
        }
    )
}