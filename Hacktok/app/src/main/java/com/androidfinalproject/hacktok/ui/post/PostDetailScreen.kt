package com.androidfinalproject.hacktok.ui.post

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.androidfinalproject.hacktok.ui.post.component.*
import org.bson.types.ObjectId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    state: PostDetailState,
    postId: ObjectId?,
    onAction: (PostDetailAction) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberLazyListState()

    LaunchedEffect(postId) {
        onAction(PostDetailAction.LoadPost(postId))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding() // Ensures proper layout when keyboard appears
            .navigationBarsPadding() // Prevents UI from overlapping with system bars
    ) {
        // Top app bar
        TopAppBar(
            title = { Text("Post") },
            navigationIcon = {
                IconButton(onClick = { onAction(PostDetailAction.NavigateBack) }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

        // Post content section
        state.post?.let { post ->
            Column(modifier = Modifier.fillMaxWidth()) {
                PostContent(
                    post = post,
                    onLikeClick = { onAction(PostDetailAction.ToggleLike) },
                    onCommentClick = { onAction(PostDetailAction.ToggleCommentSection) },
                    onShareClick = { onAction(PostDetailAction.Share) },
                    onUserClick = { userId -> onAction(PostDetailAction.OnUserClick(userId)) }
                )
            }
        }

        // Scrollable comments section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            LazyColumn(
                state = scrollState,
                modifier = Modifier.fillMaxSize()
            ) {
                // Error message if any
                state.error?.let { errorMessage ->
                    item {
                        ErrorMessage(message = errorMessage)
                    }
                }

                if (state.isCommentsVisible) {
                    item {
                        CommentSection(
                            comments = state.comments,
                            isLoading = state.isLoadingComments,
                            onUserClick = { userId -> onAction(PostDetailAction.OnUserClick(userId)) }
                        )
                    }
                }
            }
        }

        // Comment input bar at bottom
        CommentInputBar(
            commentText = state.commentText,
            onCommentTextChange = { text -> onAction(PostDetailAction.UpdateCommentText(text)) },
            onSubmitComment = {
                onAction(PostDetailAction.SubmitComment)
                focusManager.clearFocus() // Dismiss focus
                keyboardController?.hide() // Hide keyboard
            }
        )
    }
}