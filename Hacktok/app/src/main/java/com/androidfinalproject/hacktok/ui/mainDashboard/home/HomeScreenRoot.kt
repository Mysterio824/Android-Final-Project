package com.androidfinalproject.hacktok.ui.mainDashboard.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreenRoot(
    viewModel: HomeScreenViewModel = viewModel(),
    onUserClick: (String) -> Unit,
    onPostClick: (String) -> Unit,
    onStoryClick: (String) -> Unit,
    onNewPostNavigate: () -> Unit,
    onNewStoryNavigate: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeScreen(
        state = state,
        onAction = { action ->
            when(action) {
                is HomeScreenAction.OnUserClick -> onUserClick(action.userId)
                is HomeScreenAction.OnPostClick -> onPostClick(action.postId)
                is HomeScreenAction.OnStoryClick -> onStoryClick(action.storyId)
                is HomeScreenAction.OnCreateStory -> onNewStoryNavigate()
                is HomeScreenAction.OnCreatePost -> onNewPostNavigate()
                else -> viewModel.onAction(action)
            }
        }
    )
}