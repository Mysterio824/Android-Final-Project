package com.androidfinalproject.hacktok.ui.adminManage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AdminManagementScreenRoot(
    viewModel: AdminManagementViewModel = viewModel(),
    onUserNavigation: () -> Unit,
    onPostNavigation: () -> Unit,
    onCommentNavigation: () -> Unit,
    onReportNavigation: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AdminManagementScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}