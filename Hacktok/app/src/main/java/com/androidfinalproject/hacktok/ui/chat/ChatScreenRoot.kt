package com.androidfinalproject.hacktok.ui.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.post.PostDetailAction
import kotlinx.coroutines.launch

@Composable
fun ChatScreenRoot(
    navController: NavController,
    viewModel: ChatViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToManageUser: (String?) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    ChatScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is ChatAction.NavigateBack -> onNavigateBack()
                is ChatAction.NavigateToManageUser -> onNavigateToManageUser(action.userId)
                else -> viewModel.onAction(action)
            }
        }
    )
}
