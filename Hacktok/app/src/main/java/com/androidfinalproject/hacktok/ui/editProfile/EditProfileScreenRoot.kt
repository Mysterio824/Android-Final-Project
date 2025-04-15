package com.androidfinalproject.hacktok.ui.editProfile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun EditProfileScreenRoot(
    onNavigateBack: () -> Unit
) {
    val viewModel: EditProfileViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onNavigateBack()
        }
    }

    EditProfileScreen(
        state = state,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )
}