package com.androidfinalproject.hacktok.ui.adminManage.commentManagement

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CommentManagementTabRoot (
    viewModel: CommentManagementViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CommentManagementTab(
        state = state,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )
}