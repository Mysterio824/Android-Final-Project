package com.androidfinalproject.hacktok.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun UserProfileScreenRoot(
    viewModel: UserProfileViewModel,
    onChatWithFriend: (String?) -> Unit,
    onGoToPost: (String?) -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    UserProfileScreen(
        state = state,
        onAction = { action ->
            when(action) {
                is UserProfileAction.ChatWithFriend -> onChatWithFriend(state.user!!.id!!)
                is UserProfileAction.GoToPost -> onGoToPost(action.postId)
                is UserProfileAction.NavigateBack -> onNavigateBack()
                else -> viewModel.onAction(action)
            }
        }
    )
}