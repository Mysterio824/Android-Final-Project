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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.R
import com.androidfinalproject.hacktok.model.Comment
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.enums.ReportType
import com.androidfinalproject.hacktok.ui.commonComponent.PostContent
import com.androidfinalproject.hacktok.ui.commonComponent.PostOptionsContent
import com.androidfinalproject.hacktok.ui.commonComponent.ReportOptionsContent
import com.androidfinalproject.hacktok.ui.commonComponent.ShareOptionsContent
import com.androidfinalproject.hacktok.ui.commonComponent.SharePostDialog
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme
import com.androidfinalproject.hacktok.ui.post.component.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    state: PostDetailState,
    onAction: (PostDetailAction) -> Unit,
    commentId: String? = null
) {
    val commentFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()

    var showPostOptionsSheet by remember { mutableStateOf(false) }
    var showShareOptionsSheet by remember { mutableStateOf(false) }
    var selectedComment by remember { mutableStateOf<Comment?>(null) }
    var reportTargetId by remember { mutableStateOf<String?>(null) }
    var reportType by remember { mutableStateOf<ReportType?>(null) }

    var showComments by remember { mutableStateOf(true) }

    val snackbarHostState = remember { SnackbarHostState() }

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    LaunchedEffect(state.userMessage, state.error) {
        if (state.userMessage != null) {
            snackbarHostState.showSnackbar(message = state.userMessage)
        }
        if (state.error != null) {
            snackbarHostState.showSnackbar(message = state.error)
        }
        if (state.isCommenting) {
            commentFocusRequester.requestFocus()
            keyboardController?.show()
        }
        commentId?.let { id ->
            val index = state.comments.indexOfFirst { it.id == id && it.parentCommentId == null }
            if (index != -1) {
                listState.animateScrollToItem(index + 1)
            }
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                item {
                    state.post?.let { post ->
                        PostContent(
                            fullName = state.currentUser?.fullName ?: "",
                            post = post,
                            onToggleLike = { onAction(PostDetailAction.ToggleLike) },
                            onComment = { onAction(PostDetailAction.ToggleCommentInputFocus) },
                            onShare = { showShareOptionsSheet = true },
                            onOptionsClick = { showPostOptionsSheet = true },
                            onUserClick = { onAction(PostDetailAction.OnUserClick(post.userId)) },
                            onUnLike = { onAction(PostDetailAction.UnLikePost) },
                            currentId = state.currentUser?.id ?: ""
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
                    val rootComments = state.comments.filter { it.parentCommentId == null }
                    items(rootComments, key = { it.id!! }) { comment ->
                        CommentItem(
                            comment = comment,
                            isSelected = state.commentIdReply == comment.id,
                            allComments = state.comments,
                            onLikeComment = { onAction(PostDetailAction.LikeComment(it)) },
                            onUnLikeComment = { onAction(PostDetailAction.UnLikeComment(it)) },
                            onCommentLongPress = { selectedComment = comment },
                            onUserClick = { onAction(PostDetailAction.OnUserClick(it)) },
                            onReplyClick = { onAction(PostDetailAction.SelectCommentToReply(it)) },
                            currentUserId = state.currentUser!!.id!!
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
                imageUrl = state.currentUser?.profileImage,
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
                        isPostOwner = state.currentUser?.id == state.post!!.user!!.id!!,
                        onDismiss = { showPostOptionsSheet = false },
                        onReport = {
                            reportTargetId = state.post.id
                            reportType = ReportType.Post
                        }
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
                        onShareToFeed = { onAction(PostDetailAction.ShowShareDialog) },
                        onDismiss = { showShareOptionsSheet = false }
                    )
                }
            }

            if (state.showShareDialog) {
                SharePostDialog(
                    userName = state.currentUser?.fullName ?: "Unknown",
                    userAvatar = painterResource(id = R.drawable.profile_placeholder), // Replace with actual avatar if you have it
                    onDismiss = { onAction(PostDetailAction.DismissShareDialog) },
                    onSubmit = { caption, privacy ->
                        onAction(PostDetailAction.OnSharePost(post = state.post!!, caption = caption, privacy = privacy))
                        onAction(PostDetailAction.DismissShareDialog)
                    }
                )
            }

            // Comment options bottom sheet
            if (selectedComment != null) {
                ModalBottomSheet(
                    onDismissRequest = { selectedComment = null },
                    sheetState = bottomSheetState
                ) {
                    CommentOptionsContent(
                        comment = selectedComment!!,
                        onDismiss = { selectedComment = null },
                        deleteComment = { onAction(PostDetailAction.DeleteComment(selectedComment!!.id!!)) },
                        reportComment = {
                            reportTargetId = selectedComment!!.id
                            reportType = ReportType.Comment
                        },
                        isCommentOwner = selectedComment!!.userId == state.currentUser?.id
                    )
                }
            }

            // Report options bottom sheet
            if (reportTargetId != null) {
                ModalBottomSheet(
                    onDismissRequest = {
                        reportTargetId = null
                        reportType = null
                    },
                    sheetState = bottomSheetState
                ) {
                    ReportOptionsContent(
                        onDismiss = {
                            reportTargetId = null
                            reportType = null
                        },
                        targetId = reportTargetId!!,
                        onReportCauseSelected = { id, cause, type ->
                            onAction(PostDetailAction.SubmitReport(id, type, cause))
                        },
                        type = reportType!!,
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