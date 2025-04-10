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
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeScreen(
        state = state,
        onAction = { action ->
            when(action) {
                is HomeScreenAction.OnUserClick -> onUserClick(action.userId)
                is HomeScreenAction.OnPostClick -> onPostClick(action.postId)
                else -> viewModel.onAction(action)
            }
        }
    )
}