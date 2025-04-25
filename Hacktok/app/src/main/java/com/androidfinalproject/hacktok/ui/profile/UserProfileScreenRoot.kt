package com.androidfinalproject.hacktok.ui.profile

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun UserProfileScreenRoot(
    viewModel: UserProfileViewModel = hiltViewModel(),
    userId: String,
    onChatWithFriend: (String) -> Unit,
    onGoToPost: (String) -> Unit,
    onGoToFriendList: () -> Unit,
    onNavigateBack: () -> Unit,
    onUserNavigate: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val TAG = "UserProfileScreenRoot"
    
    // Log when state changes for debugging
    LaunchedEffect(userId) {
        Log.d(TAG, "LaunchedEffect triggered for userId: $userId - Loading profile.")
        viewModel.loadUserProfile(userId)
    }

    UserProfileScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is UserProfileAction.NavigateBack -> onNavigateBack()
                is UserProfileAction.NavigateFriendList -> onGoToFriendList()
                is UserProfileAction.MessageUser -> onChatWithFriend(userId)
                is UserProfileAction.GoToPost -> onGoToPost(action.postId)
                is UserProfileAction.OnUserClick -> onUserNavigate(action.userId)
                else -> viewModel.onAction(action)
            }
        }
    )
}