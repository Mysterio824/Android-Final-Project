package com.androidfinalproject.hacktok.ui.adminManage.postManagement

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PostManagementTabRoot(
    viewModel: PostManagementViewModel
){
    val state by viewModel.state.collectAsStateWithLifecycle()

    PostManagementTab(
        state = state,
        onAction = {action ->
            viewModel.onAction(action)
        }
    )

}