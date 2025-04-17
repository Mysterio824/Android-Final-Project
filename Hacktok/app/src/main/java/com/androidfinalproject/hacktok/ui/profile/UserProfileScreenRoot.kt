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
    onGoToFriendList: (String) -> Unit,
    onNavigateBack: () -> Unit
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
            try {
                Log.d(TAG, "Action: $action")
                when(action) {
                    is UserProfileAction.ChatWithFriend -> {
                        val uid = state.user?.id
                        if (uid != null) {
                            onChatWithFriend(uid)
                        } else {
                            Log.e(TAG, "Cannot chat with friend: User ID is null")
                        }
                    }
                    is UserProfileAction.GoToPost -> onGoToPost(action.postId)
                    is UserProfileAction.NavigateFriendList -> {
                        val uid = state.user?.id
                        if (uid != null) {
                            onGoToFriendList(uid)
                        } else {
                            Log.e(TAG, "Cannot navigate to friend list: User ID is null")
                        }
                    }
                    is UserProfileAction.NavigateBack -> onNavigateBack()
                    else -> viewModel.onAction(action)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling action $action", e)
            }
        }
    )
}