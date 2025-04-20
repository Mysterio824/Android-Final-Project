package com.androidfinalproject.hacktok.ui.storydetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun StoryDetailScreenRoot(
    userId :String?=null,
    viewModel: StoryDetailViewModel = hiltViewModel(),
    storyId: String? = null,
    onClose: () -> Unit,
    onNavigateToUserProfile: (String?) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(storyId) {
        viewModel.onAction(StoryDetailAction.LoadStoryDetails(userId))
    }

    StoryDetailScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is StoryDetailAction.CloseStory -> onClose()
                is StoryDetailAction.NavigateToUserProfile -> onNavigateToUserProfile(action.userId)
                else -> viewModel.onAction(action)
            }
        }
    )
}