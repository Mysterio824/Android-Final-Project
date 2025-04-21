package com.androidfinalproject.hacktok.ui.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.post.PostDetailAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@Composable
fun ChatScreenRoot(
    userId: String,
    viewModel: ChatViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onChatOptionNavigate: (String) -> Unit,
    onNavigateToManageUser: (String?) -> Unit = {}
) {
    LaunchedEffect(userId) {
        viewModel.setUserId(userId)
    }

    val state by viewModel.state.collectAsState()

    ChatScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is ChatAction.NavigateBack -> onNavigateBack()
                is ChatAction.NavigateToManageUser -> onNavigateToManageUser(action.userId)
                is ChatAction.ChatOptionNavigate -> onChatOptionNavigate(action.chatId)
                else -> viewModel.onAction(action)
            }
        }
    )
}
