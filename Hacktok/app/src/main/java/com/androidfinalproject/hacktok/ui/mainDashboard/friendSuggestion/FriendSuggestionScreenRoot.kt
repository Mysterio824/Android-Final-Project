package com.androidfinalproject.hacktok.ui.mainDashboard.friendSuggestion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.androidfinalproject.hacktok.ui.mainDashboard.DashboardAction

@Composable
fun FriendSuggestionScreenRoot(
    viewModel: FriendSuggestionViewModel,
    onUserNavigate: (String) -> Unit,
    onFriendListNavigate: (String) -> Unit,
){
    val state by viewModel.state.collectAsStateWithLifecycle()

    FriendSuggestionScreen(
        state = state,
        onAction = { action ->
            when(action){
                is FriendSuggestionAction.OnUserClick -> onUserNavigate(action.userId)
                is FriendSuggestionAction.OnFriendListNavigate -> onFriendListNavigate(state.user!!.id!!)
                else -> viewModel.onAction(action)
            }
        }
    )
}