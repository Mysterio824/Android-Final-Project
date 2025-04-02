package com.androidfinalproject.hacktok.ui.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun ChatScreenRoot(
    navController: NavController,
    viewModel: ChatViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadInitialMessages()
    }

    ChatScreen(
        messages = state.messages,
        currentUserId = state.currentUser.username,
        onSendMessage = { content ->
            viewModel.sendMessage(content)
        },
        onDeleteMessage = { messageId ->
            scope.launch {
                viewModel.deleteMessage(messageId)
            }
        },
        onBackClick = {
            navController.popBackStack()
        },
        onInfoClick = {
            navController.navigate("manage_user/${state.otherUser.username}")
        }
    )
}
