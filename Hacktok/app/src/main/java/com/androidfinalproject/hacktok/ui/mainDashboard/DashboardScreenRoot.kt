package com.androidfinalproject.hacktok.ui.mainDashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DashboardScreenRoot(
    viewModel: DashboardViewModel = viewModel(),
    onUserProfileNavigate: (String?) -> Unit = {},
    onPostDetailNavigate: (String?) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DashboardScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is DashboardAction.UserClick -> onUserProfileNavigate(action.userId)
                is DashboardAction.PostClick -> onPostDetailNavigate(action.postId)
                else -> viewModel.onAction(action)
            }
        }
    )
}