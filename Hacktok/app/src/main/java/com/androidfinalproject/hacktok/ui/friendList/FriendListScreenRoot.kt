package com.androidfinalproject.hacktok.ui.friendList

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.androidfinalproject.hacktok.model.User
import org.bson.types.ObjectId

@Composable
fun FriendListScreenRoot(
    viewModel: FriendListViewModel,
    onChatWithFriend: (String) -> Unit,
    onUserProfileView: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    FriendListScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is FriendListAction.ChatWithFriend -> onChatWithFriend(action.userId)
                is FriendListAction.UserClicked -> onUserProfileView(action.userId)
                else -> viewModel.onAction(action)
            }
        }
    )
}