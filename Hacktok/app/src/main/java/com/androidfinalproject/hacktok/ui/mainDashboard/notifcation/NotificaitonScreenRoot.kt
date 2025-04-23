package com.androidfinalproject.hacktok.ui.mainDashboard.notifcation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NotificationScreenRoot(
    viewModel: NotificationViewModel = hiltViewModel(),
    onPostClick: (String, String?) -> Unit,
    onUserClick: (String) -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.navigateComment) {
        if(state.navigateComment){
            onPostClick(state.postId, state.commentId)
        }
    }

    NotificationScreen(
        state= state,
        onAction = { action ->
            when(action) {
                is NotificationAction.OnUserClick -> onUserClick(action.userId)
                is NotificationAction.OnPostClick -> onPostClick(action.postId, null)
                is NotificationAction.OnNavigationBack -> onNavigateBack()
                else -> viewModel.onAction(action)
            }
        }
    )
}