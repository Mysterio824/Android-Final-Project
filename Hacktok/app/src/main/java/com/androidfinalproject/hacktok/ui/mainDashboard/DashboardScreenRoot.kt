package com.androidfinalproject.hacktok.ui.mainDashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.androidfinalproject.hacktok.ui.mainDashboard.currentProfile.CurrentProfileAction
import com.androidfinalproject.hacktok.ui.mainDashboard.messageDashboard.MessageDashboardAction

@Composable
fun DashboardScreenRoot(
    viewModel: DashboardViewModel = viewModel(),
    onUserProfileNavigate: (String?) -> Unit = {},
    onPostDetailNavigate: (String?) -> Unit = {},
    onUserChatNavigate: (String?) -> Unit,
    onGroupChatNavigate: (String?) -> Unit,
    onFriendListNavigate: (String) -> Unit,
    onEditProfileNavigate: () -> Unit,
    onPostEditNavigate: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DashboardScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is DashboardAction.OnUserClick -> onUserProfileNavigate(action.userId)
                is DashboardAction.OnPostClick -> onPostDetailNavigate(action.postId)
                is DashboardAction.GotoUserChat -> onUserChatNavigate(action.userId)
                is DashboardAction.GotoGroupChat -> onGroupChatNavigate(action.groupId)
                is DashboardAction.OnNavigateBack -> System.exit(0)
                else -> viewModel.onAction(action)
            }
        }
    )
}