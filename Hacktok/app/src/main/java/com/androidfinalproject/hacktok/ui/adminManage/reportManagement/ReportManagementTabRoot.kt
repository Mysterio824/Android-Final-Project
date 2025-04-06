package com.androidfinalproject.hacktok.ui.adminManage.reportManagement

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ReportManagementTabRoot(
    viewModel: ReportManagementViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ReportManagementTab(
        state = state,
        onAction = {action ->
            viewModel.onAction(action)
        }
    )


}