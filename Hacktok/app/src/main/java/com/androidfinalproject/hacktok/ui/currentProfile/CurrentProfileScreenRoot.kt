package com.androidfinalproject.hacktok.ui.currentProfile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.androidfinalproject.hacktok.ui.friendList.FriendListAction
import com.androidfinalproject.hacktok.ui.mainDashboard.home.HomeScreenAction
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

@Composable
fun CurrentProfileScreenRoot(
    onPostClickNavigation: (String) -> Unit,
    onPostEditNavigation: (String) -> Unit,
    onNewPostNavigation: () -> Unit,
    onFriendListNavigation: (String) -> Unit,
    onProfileEditNavigation: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CurrentProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CurrentProfileScreen(
        state = state,
        onAction = {action ->
            when(action) {
                is CurrentProfileAction.OnNavigateBack -> onNavigateBack()
                is CurrentProfileAction.NavigateToProfileEdit -> onProfileEditNavigation()
                is CurrentProfileAction.NavigateToNewPost -> onNewPostNavigation()
                is CurrentProfileAction.NavigateToPostEdit -> onPostEditNavigation(action.postId)
                is CurrentProfileAction.OnPostClick -> onPostClickNavigation(action.post.id!!)
                is CurrentProfileAction.NavigateFriendList -> onFriendListNavigation(action.userId)
                else -> viewModel.onAction(action)
            }
        },
    )
}