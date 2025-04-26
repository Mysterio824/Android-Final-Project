package com.androidfinalproject.hacktok.ui.createAd

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.androidfinalproject.hacktok.ui.createAd.CreateAdViewModel

@Composable
fun CreateAdScreenRoot(
    viewModel: CreateAdViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onAdCreated: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onAdCreated()
        }
    }

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