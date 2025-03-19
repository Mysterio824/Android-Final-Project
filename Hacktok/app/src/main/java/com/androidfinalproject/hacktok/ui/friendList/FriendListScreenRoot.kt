package com.androidfinalproject.hacktok.ui.friendList

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.bson.types.ObjectId

@Composable
fun FriendListScreenRoot(
    viewModel: FriendListViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onChatWithFriend: (ObjectId?) -> Unit,
    onUserProfileView: (ObjectId?) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    FriendListScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is FriendListAction.NavigateBack -> onNavigateBack()
                is FriendListAction.ChatWithFriend -> onChatWithFriend(action.userId)
                is FriendListAction.UserClicked -> onUserProfileView(action.userId)
                else -> viewModel.onAction(action)
            }
        }
    )
}