package com.androidfinalproject.hacktok.ui.post

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme
import com.androidfinalproject.hacktok.ui.post.component.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    state: PostDetailState,
    onAction: (PostDetailAction) -> Unit
) {
    val commentFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    var showPostOptionsSheet by remember { mutableStateOf(false) }
    var showShareOptionsSheet by remember { mutableStateOf(false) }
    var selectedCommentId by remember { mutableStateOf<String?>(null) }

    var showComments by remember { mutableStateOf(true) }

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    LaunchedEffect(state.isCommenting) {
        if (state.isCommenting) {
            commentFocusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    Scaffold(
        topBar = {
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
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                item {
                    state.post?.let { post ->
                        PostContent(
                            post = post,
                            onToggleLike = { onAction(PostDetailAction.ToggleLike) },
                            onComment = { onAction(PostDetailAction.ToggleCommentInputFocus) },
                            onShare = { showShareOptionsSheet = true },
                            onOptionsClick = { showPostOptionsSheet = true },
                            onUserClick = { onAction(PostDetailAction.OnUserClick(post.userId)) }
                        )

                        CommentsSectionToggle(
                            commentCount = post.commentCount,
                            showComments = showComments,
                            onToggle = { showComments = !showComments }
                        )
                    }
                }


                // Comments section
                if (showComments) {
                    // Root level comments
                    val rootComments = state.comments.filter { it.parentCommentId == null }
                    items(rootComments) { comment ->
                        CommentItem(
                            comment = comment,
                            allComments = state.comments,
                            onLikeComment = { commentId -> onAction(PostDetailAction.LikeComment(commentId)) },
                            onCommentLongPress = { commentId -> selectedCommentId = commentId },
                            onUserClick = { userId -> onAction(PostDetailAction.OnUserClick(userId)) },
                            onReplyClick = {
                                onAction(PostDetailAction.ToggleCommentInputFocus)
                                // In a real app, you'd also track which comment is being replied to
                            }
                        )
                    }
                }

                // Loading indicator for comments
                if (state.isLoadingComments) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Loading comments...")
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(70.dp))
                }
            }

            CommentInputBar(
                text = state.commentText,
                onTextChange = { onAction(PostDetailAction.UpdateCommentText(it)) },
                onSubmit = { onAction(PostDetailAction.SubmitComment) },
                focusRequester = commentFocusRequester,
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            if (showPostOptionsSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showPostOptionsSheet = false },
                    sheetState = bottomSheetState
                ) {
                    PostOptionsContent(
                        isPostOwner = state.isPostOwner,
                        onDismiss = { showPostOptionsSheet = false }
                    )
                }
            }

            // Share options bottom sheet
            if (showShareOptionsSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showShareOptionsSheet = false },
                    sheetState = bottomSheetState
                ) {
                    ShareOptionsContent(
                        onDismiss = { showShareOptionsSheet = false }
                    )
                }
            }

            // Comment options bottom sheet
            if (selectedCommentId != null) {
                ModalBottomSheet(
                    onDismissRequest = { selectedCommentId = null },
                    sheetState = bottomSheetState
                ) {
                    CommentOptionsContent(
                        commentId = selectedCommentId,
                        onDismiss = { selectedCommentId = null }
                    )
                }
            }
        }
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