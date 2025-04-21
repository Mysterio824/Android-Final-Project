package com.androidfinalproject.hacktok.ui.chatDetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun ChatDetailScreenRoot(
    userId: String,
    isGroup: Boolean,
    viewModel: ChatDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onUserProfileNavigate: (String?) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(userId) {
        viewModel.setUserId(userId)
    }

    ChatDetailScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is ChatDetailAction.NavigateBack -> onNavigateBack()
                is ChatDetailAction.NavigateToUserProfile -> onUserProfileNavigate(action.userId)
                else -> viewModel.onAction(action)
            }
        }
    )
}