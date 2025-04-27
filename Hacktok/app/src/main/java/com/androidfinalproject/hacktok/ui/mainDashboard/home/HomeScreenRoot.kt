package com.androidfinalproject.hacktok.ui.mainDashboard.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreenRoot(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    onUserClick: (String) -> Unit,
    onPostClick: (String) -> Unit,
    onStoryClick: (String) -> Unit,
    onNewPostNavigate: () -> Unit,
    onCreateStoryNavigate: () -> Unit,
    onPostEditClick: (String) -> Unit,
    onImageClickNavigate: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeScreen(
        state = state,
        onAction = { action ->
            when(action) {
                is HomeScreenAction.OnUserClick -> onUserClick(action.userId)
                is HomeScreenAction.OnPostClick -> onPostClick(action.postId)
                is HomeScreenAction.OnStoryClick -> onStoryClick(action.storyId)
                is HomeScreenAction.OnPostEditClick -> onPostEditClick(action.postId)
                is HomeScreenAction.OnCreateStory -> onCreateStoryNavigate()
                is HomeScreenAction.OnCreatePost -> onNewPostNavigate()
                is HomeScreenAction.OnImageClick -> onImageClickNavigate(action.imageUrl)
                else -> viewModel.onAction(action)
            }
        }
    )
}