package com.androidfinalproject.hacktok.ui.messageDashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.androidfinalproject.hacktok.model.User

@Composable
fun MessageDashboardRoot (
    viewModel: MessageDashboardViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onGoToChat: (String?) -> Unit,
    onNewChat: () -> Unit,
    onNewGroup: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MessageDashboardScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is MessageDashboardAction.NavigateBack -> onNavigateBack()
                is MessageDashboardAction.GoToChat -> onGoToChat(action.userId)
                is MessageDashboardAction.NewGroup -> onNewGroup()
                is MessageDashboardAction.NewChat -> onNewChat()
                else -> viewModel.onAction(action)
            }
        }
    )
}