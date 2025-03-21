package com.androidfinalproject.hacktok.ui.post

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.bson.types.ObjectId

@Composable
fun PostDetailScreenRoot(
    postId: ObjectId? = null,
    viewModel: PostDetailViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onUserProfileNavigate: (ObjectId?) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PostDetailScreen(
        state = state,
        postId = postId,
        onAction = { action ->
            when (action) {
                is PostDetailAction.NavigateBack -> onNavigateBack()
                is PostDetailAction.OnUserClick -> onUserProfileNavigate(action.user.id)
                else -> viewModel.onAction(action)
            }
        }
    )
}
