package com.androidfinalproject.hacktok.ui.mainDashboard.currentProfile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.androidfinalproject.hacktok.ui.mainDashboard.home.HomeScreenAction

@Composable
fun CurrentProfileScreenRoot(
    viewModel: CurrentProfileViewModel,
    onPostEditNavigation: (String) -> Unit,
    onProfileEditNavigation: () -> Unit,
    onPostClickNavigation: (String) -> Unit,
    onFriendListNavigation: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    CurrentProfileScreen(
        state = state,
        onAction = { action ->
            when(action){
                is CurrentProfileAction.OnPostClick -> onPostClickNavigation(action.post.id!!)
                is CurrentProfileAction.NavigateToPostEdit -> onPostEditNavigation(action.post.id!!)
                is CurrentProfileAction.NavigateToProfileEdit -> onProfileEditNavigation()
                is CurrentProfileAction.NavigateFriendList -> onFriendListNavigation(state.user!!.id!!)
            }

        }
    )
}