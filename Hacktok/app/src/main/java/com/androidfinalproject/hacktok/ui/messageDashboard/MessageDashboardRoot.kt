package com.androidfinalproject.hacktok.ui.messageDashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MessageDashboardRoot (
    viewModel: MessageDashboardViewModel = viewModel(),
    onGoToChat: (String?) -> Unit,
    onNewChat: (String?) -> Unit,
    onNewGroup: (String?) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MessageDashboardScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is MessageDashboardAction.GoToChat -> onGoToChat(action.userId)
                is MessageDashboardAction.GoToNewGroupChat -> onNewGroup(action.groupId)
                is MessageDashboardAction.GoToNewChat -> onNewChat(action.userId)
                is MessageDashboardAction.OnNavigateBack -> onNavigateBack()
                else -> viewModel.onAction(action)
            }
        }
    )
}