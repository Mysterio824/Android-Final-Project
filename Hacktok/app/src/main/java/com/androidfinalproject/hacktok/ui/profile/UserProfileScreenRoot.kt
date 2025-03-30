package com.androidfinalproject.hacktok.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun UserProfileScreenRoot(
    viewModel: UserProfileViewModel = viewModel(),
    userId: String?,
    onNavigateBack: () -> Unit,
    onBlockUser: (String?) -> Unit,
    onChatWithFriend: (String?) -> Unit,
    onGoToPost: (String?) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Load the profile based on userId
    LaunchedEffect(userId) {
        viewModel.loadUserProfile(userId)
    }

    if (state.user != null) {
        UserProfileScreen(
            user = state.user!!,
            posts = state.posts,
            isFriend = state.isFriend,
            isBlocked = state.isBlocked,
            onSendFriendRequest = { viewModel.onAction(UserProfileAction.AddFriend(userId)) },
            onUnfriend = { viewModel.onAction(UserProfileAction.Unfriend(userId)) },
            onChat = { onChatWithFriend(userId) },
            onBlock = { onBlockUser(userId) }
        )
    } else if (state.isLoading) {
        // Show loading UI
    } else if (state.error != null) {
        // Show error UI
    }
}