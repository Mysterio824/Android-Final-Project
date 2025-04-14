package com.androidfinalproject.hacktok.ui.newPost

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState

@Composable
fun NewPostRoot() {
    val viewModel: NewPostViewModel = viewModel()
    val state = viewModel.state.collectAsState()

    NewPostScreen(
        state = state.value,
        onAction = viewModel::onAction
    )
}