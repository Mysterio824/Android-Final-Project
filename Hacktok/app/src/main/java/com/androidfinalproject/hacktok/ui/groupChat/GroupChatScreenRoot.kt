package com.androidfinalproject.hacktok.ui.groupChat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun GroupChatScreenRoot(
    navController: NavController,
    viewModel: GroupChatViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToManageGroup: (String?) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    GroupChatScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is GroupChatAction.NavigateBack -> onNavigateBack()
                is GroupChatAction.NavigateToManageGroup -> onNavigateToManageGroup(action.groupId)
                else -> viewModel.onAction(action)
            }
        }
    )
}
