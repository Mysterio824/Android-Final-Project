package com.androidfinalproject.hacktok.ui.chatDetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun ChatDetailScreenRoot(
    chatId: String,
    isGroup: Boolean,
    viewModel: ChatDetailViewModel = viewModel(
        factory = ChatDetailViewModelFactory(chatId, isGroup)
    ),
    onNavigateBack: () -> Unit,
    onUserProfileNavigate: (String?) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

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