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
    onStatisticNavigation: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AdminManagementScreen(
        state = state,
        onAction = { action ->
            when(action) {
                is AdminManagementAction.NavigateToStatistics
                    -> onStatisticNavigation()

                is AdminManagementAction.OnNavigateBack
                    -> System.exit(0)

                else -> viewModel.onAction(action)
            }

        }

    )
}