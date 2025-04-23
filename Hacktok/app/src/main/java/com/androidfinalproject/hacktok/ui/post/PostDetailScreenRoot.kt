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

    LaunchedEffect(commentId, state.comments.isNotEmpty()) {
        if (commentId != null && state.comments.isNotEmpty()) {
            // Check if the comment exists in the loaded comments
            val commentExists = state.comments.any { it.id == commentId }
            if (commentExists) {
                // Set the selected comment for highlighting
                viewModel.onAction(PostDetailAction.SelectCommentToHighlight(commentId))
                viewModel.onAction(PostDetailAction.SetCommentsVisible(true))
            }
        }
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