package com.androidfinalproject.hacktok.ui.mainDashboard.friendSuggestion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.androidfinalproject.hacktok.model.User

@Composable
fun FriendSuggestionScreenRoot(
    viewModel: FriendSuggestionViewModel,
    currentUser: User,
    onUserNavigate: (String) -> Unit,
    onFriendListNavigate: (String) -> Unit,
){
    // Initialize the ViewModel with the current user
    LaunchedEffect(currentUser.id) {
        viewModel.initialize(currentUser)
    }
    
    val state by viewModel.state.collectAsStateWithLifecycle()

    FriendSuggestionScreen(
        state = state,
        onAction = { action ->
            when(action){
                is FriendSuggestionAction.OnUserClick -> onUserNavigate(action.userId)
                is FriendSuggestionAction.OnFriendListNavigate -> onFriendListNavigate(currentUser.id ?: "")
                else -> viewModel.onAction(action)
            }
        }
    )
}