package com.androidfinalproject.hacktok.ui.adminManage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AdminManagementScreenRoot(
    viewModel: AdminManagementViewModel = viewModel(),
    onUserNavigation: () -> Unit,
    onPostNavigation: () -> Unit,
    onCommentNavigation: () -> Unit,
) {
    val state = viewModel.state.collectAsState().value

    AdminManagementScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}