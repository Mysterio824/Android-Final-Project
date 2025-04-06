package com.androidfinalproject.hacktok.ui.post

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PostDetailScreenRoot(
    viewModel: PostDetailViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onUserProfileNavigate: (String?) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PostDetailScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is PostDetailAction.NavigateBack -> onNavigateBack()
                is PostDetailAction.OnUserClick -> onUserProfileNavigate(action.userId)
                else -> viewModel.onAction(action)
            }
        }
    )
}
