// ManageUserScreenRoot.kt
package com.androidfinalproject.hacktok.ui.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.androidfinalproject.hacktok.model.User

@Composable
fun ManageUserScreenRoot(
    navController: NavController,
    userId: String?,
    viewModel: ChatViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    ManageUserScreen(
        user = state.otherUser,
        isMuted = state.isUserMuted,
        onToggleMute = { viewModel.onAction(ChatAction.ToggleMute) },
        onCreateGroup = { viewModel.onAction(ChatAction.CreateGroup) },
        onFindInChat = { viewModel.onAction(ChatAction.FindInChat) },
        onDeleteChat = { viewModel.onAction(ChatAction.DeleteChat) },
        onBlockUser = { viewModel.onAction(ChatAction.BlockUser) }
    )
}