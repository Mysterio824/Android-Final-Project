package com.androidfinalproject.hacktok.ui.post

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.ui.post.component.*
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    state: PostDetailState,
    onAction: (PostDetailAction) -> Unit,
) {
    val scrollState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding()
    ) {
        TopAppBar(
            title = { Text("Post") },
            navigationIcon = {
                IconButton(onClick = { onAction(PostDetailAction.NavigateBack) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            LazyColumn(
                state = scrollState,
                modifier = Modifier.fillMaxSize()
            ) {
                state.post?.let { post ->
                    item {
                        PostContent(
                            post = post,
                            onLikeClick = { onAction(PostDetailAction.ToggleLike) },
                            onCommentClick = { onAction(PostDetailAction.ToggleCommentInputFocus) },
                            onShareClick = { onAction(PostDetailAction.Share) },
                            onUserClick = { userId -> onAction(PostDetailAction.OnUserClick(userId)) }
                        )
                    }

                    item {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }

                state.error?.let { errorMessage ->
                    item {
                        ErrorMessage(message = errorMessage)
                    }
                }

                item {
                    CommentSection(
                        comments = state.comments,
                        isLoading = state.isLoadingComments,
                        onUserClick = { userId -> onAction(PostDetailAction.OnUserClick(userId)) },
                        onLikeClick = { commentId -> onAction(PostDetailAction.LikeComment(commentId)) },
                        isLikedByUser = { true }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        CommentInputBar(
            commentText = state.commentText,
            onSubmit = {
                onAction(PostDetailAction.SubmitComment)
                // Optionally, you can clear focus after submitting
                // onAction(PostDetailAction.SetCommentFocus(false))
            },
            onTextChange = { text ->
                onAction(PostDetailAction.UpdateCommentText(text))
            },
            isFocused = state.isCommenting,
            onFocusChanged = { isFocused ->
                // Update ViewModel when focus changes
                onAction(PostDetailAction.SetCommentFocus(isFocused))
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PostDetailScreenPreview() {
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            PostDetailScreen(
                state = PostDetailState(
                    post = MockData.mockPosts.first(),
                    comments = MockData.mockComments
                ),
                onAction = {},
            )
        }
    }
}