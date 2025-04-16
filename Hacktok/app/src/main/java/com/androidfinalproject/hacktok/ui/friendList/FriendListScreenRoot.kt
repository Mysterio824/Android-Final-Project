package com.androidfinalproject.hacktok.ui.friendList

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.androidfinalproject.hacktok.model.User
import org.bson.types.ObjectId

@Composable
fun FriendListScreenRoot(
    viewModel: FriendListViewModel = hiltViewModel(),
    userId: String,
    onChatWithFriend: (String) -> Unit,
    onUserProfileView: (String) -> Unit
) {
    // Initialize the ViewModel with the userId
    LaunchedEffect(userId) {
        viewModel.initialize(userId)
    }
    
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