package com.androidfinalproject.hacktok.ui.post

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.androidfinalproject.hacktok.repository.PostRepository

@Composable
fun PostDetailScreenRoot(
    viewModel: PostDetailViewModel = hiltViewModel(),
    postId: String,
    commentId: String? = null,
    onNavigateBack: () -> Unit,
    onUserProfileNavigate: (String?) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(postId) {
        viewModel.onAction(PostDetailAction.LoadPost(postId))
    }

    PostDetailScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is PostDetailAction.NavigateBack -> onNavigateBack()
                is PostDetailAction.OnUserClick -> onUserProfileNavigate(action.userId)
                else -> viewModel.onAction(action)
            }
        }
    )
}