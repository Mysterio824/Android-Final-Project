package com.androidfinalproject.hacktok.ui.adminManage.userManagement

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun UserManagementTabRoot(
    viewModel: UserManagementViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    UserManagementTab(
        state = state,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )
}