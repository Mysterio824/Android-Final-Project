package com.androidfinalproject.hacktok.ui.mainDashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun DashboardScreenRoot(
    viewModel: DashboardViewModel = hiltViewModel(),
    onUserProfileNavigate: (String?) -> Unit = {},
    onPostDetailNavigate: (String?, String?) -> Unit,
    onStoryNavigate: (String?) -> Unit = {},
    onUserChatNavigate: (String?) -> Unit,
    onGroupChatNavigate: (String?) -> Unit,
    onFriendListNavigate: (String) -> Unit,
    onMessageDashBoardNavigate: () -> Unit,
    onCurrentProfileNavigate: () -> Unit,
    onSearchNavigate: () -> Unit,
    onCreatePostNavigate: () -> Unit,
    onCreateStoryNavigate: () -> Unit,
    onAuthNavigate: () -> Unit,
    onPostEditNavigate: (String) -> Unit,
    onUserEditNavigate: () -> Unit,
    onChangePassNavigate: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DashboardScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is DashboardAction.OnUserClick -> onUserProfileNavigate(action.userId)
                is DashboardAction.OnPostClick -> onPostDetailNavigate(action.postId, action.commentId)
                is DashboardAction.GotoUserChat -> onUserChatNavigate(action.userId)
                is DashboardAction.GotoGroupChat -> onGroupChatNavigate(action.groupId)
                is DashboardAction.OnFriendListNavigate -> onFriendListNavigate(action.userId)
                is DashboardAction.OnStoryClick -> onStoryNavigate(action.storyId)
                is DashboardAction.OnCreatePost -> onCreatePostNavigate()
                is DashboardAction.OnCreateStory -> onCreateStoryNavigate()
                is DashboardAction.OnSearchNavigate -> onSearchNavigate()
                is DashboardAction.OnPostEditNavigate -> onPostEditNavigate(action.postId)
                is DashboardAction.OnCurrentProfileNavigate -> onCurrentProfileNavigate()
                is DashboardAction.OnMessageDashboardNavigate -> onMessageDashBoardNavigate()
                is DashboardAction.OnUserEdit -> onUserEditNavigate()
                is DashboardAction.OnChangePass -> onChangePassNavigate()
                is DashboardAction.OnAuthNavigate -> onAuthNavigate()
                is DashboardAction.OnNavigateBack -> onNavigateBack()
                else -> viewModel.onAction(action)
            }
        }
    )
}