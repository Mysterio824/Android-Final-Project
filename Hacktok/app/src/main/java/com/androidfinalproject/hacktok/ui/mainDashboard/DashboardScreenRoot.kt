package com.androidfinalproject.hacktok.ui.mainDashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.system.exitProcess

@Composable
fun DashboardScreenRoot(
    viewModel: DashboardViewModel = hiltViewModel(),
    onUserProfileNavigate: (String?) -> Unit = {},
    onPostDetailNavigate: (String?) -> Unit = {},
    onStoryNavigate: (String?) -> Unit = {},
    onUserChatNavigate: (String?) -> Unit,
    onGroupChatNavigate: (String?) -> Unit,
    onFriendListNavigate: (String) -> Unit,
    onMessageDashBoardNavigate: () -> Unit,
    onCurrentProfileNavigate: () -> Unit,
    onSearchNavigate: () -> Unit,
    onCreatePostNavigate: () -> Unit,
    onCreateStoryNavigate: () -> Unit,
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
                is DashboardAction.OnFriendListNavigate -> onFriendListNavigate(action.userId)
                is DashboardAction.OnStoryClick -> onStoryNavigate(action.storyId)
                is DashboardAction.OnCreatePost -> onCreatePostNavigate()
                is DashboardAction.OnCreateStory -> onCreateStoryNavigate()
                is DashboardAction.OnSearchNavigate -> onSearchNavigate()
                is DashboardAction.OnCurrentProfileNavigate -> onCurrentProfileNavigate()
                is DashboardAction.OnMessageDashboardNavigate -> onMessageDashBoardNavigate()
                is DashboardAction.OnNavigateBack -> exitProcess(0)
                else -> viewModel.onAction(action)
            }
        }
    )
}