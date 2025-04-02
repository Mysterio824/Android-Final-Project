package com.androidfinalproject.hacktok.ui.editProfile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun EditProfileScreenRoot(
    viewModel: EditProfileViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()


    EditProfileScreen(
        state = state,
        onAction = {action ->
            viewModel.onAction(action)
        }
    )
}