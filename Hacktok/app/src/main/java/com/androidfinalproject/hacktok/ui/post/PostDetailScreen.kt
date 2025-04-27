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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.Comment
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.enums.ReportType
import com.androidfinalproject.hacktok.ui.commonComponent.LikeListContent
import com.androidfinalproject.hacktok.ui.commonComponent.PostContent
import com.androidfinalproject.hacktok.ui.commonComponent.PostOptionsContent
import com.androidfinalproject.hacktok.ui.commonComponent.ReportOptionsContent
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
    var selectedComment by remember { mutableStateOf<Comment?>(null) }
    var reportTargetId by remember { mutableStateOf<String?>(null) }
    var reportType by remember { mutableStateOf<ReportType?>(null) }
    var selectedLikeShowId by remember { mutableStateOf<String?>(null) }
    var selectedType by remember { mutableStateOf<String?>(null) }
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
    }

    LaunchedEffect(state.trigger) {
        if (state.isCommenting) {
            commentFocusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    LaunchedEffect(commentId, state.comments, state.highlightedCommentId, state.showComments) {
        if (state.showComments && state.highlightedCommentId != null) {
            val commentList = state.comments
            val index = commentList.indexOfFirst { it.id == state.highlightedCommentId && it.parentCommentId == null }

            if (index != -1) {
                val scrollPosition = index + 2
                listState.animateScrollToItem(scrollPosition-2)
            } else {
                val replyComment = commentList.find { it.id == state.highlightedCommentId }
                if (replyComment?.parentCommentId != null) {
                    val parentIndex = commentList.indexOfFirst {
                        it.id == replyComment.parentCommentId && it.parentCommentId == null
                    }

                    if (parentIndex != -1) {
                        listState.animateScrollToItem(parentIndex-2)
                    }
                }
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
                            post = post,
                            user = state.postUser!!,
                            referencePost = state.referencePost,
                            referenceUser = state.referenceUser,
                            onToggleLike = { emoji -> onAction(PostDetailAction.ToggleLike(emoji)) },
                            onComment = { onAction(PostDetailAction.ToggleCommentInputFocus) },
                            onShare = { onAction(PostDetailAction.ShowShareDialog) },
                            onOptionsClick = { showPostOptionsSheet = true },
                            onUserClick = { onAction(PostDetailAction.OnUserClick(post.userId)) },
                            onUnLike = { onAction(PostDetailAction.UnLikePost) },
                            currentId = state.currentUser?.id ?: "",
                            onLikesClick = {
                                selectedType = "post"
                                selectedLikeShowId = it
                            },
                            onImageClick = { onAction(PostDetailAction.OnImageClick(it)) }
                        )

                        CommentsSectionToggle(
                            commentCount = post.commentCount,
                            showComments = state.showComments,
                            onToggle = { onAction(PostDetailAction.SetCommentsVisible(!state.showComments)) }
                        )
                    }
                }

                // Comments section
                if (state.showComments) {
                    val rootComments = state.comments.filter { it.parentCommentId == null }
                    items(rootComments, key = { it.id!! }) { comment ->
                        CommentItem(
                            comment = comment,
                            isSelected = state.commentIdReply == comment.id,
                            isHighlighted = state.highlightedCommentId == comment.id,
                            allComments = state.comments,
                            onLikeComment = { commentId, emoji -> onAction(PostDetailAction.LikeComment(commentId, emoji)) },
                            onUnLikeComment = { onAction(PostDetailAction.UnLikeComment(it)) },
                            onCommentLongPress = { selectedComment = comment },
                            onUserClick = { onAction(PostDetailAction.OnUserClick(it)) },
                            onReplyClick = { onAction(PostDetailAction.SelectCommentToReply(it)) },
                            currentUserId = state.currentUser!!.id!!,
                            onLikesClick = {
                                selectedType = "comment"
                                selectedLikeShowId = it
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
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = MaterialTheme.colorScheme.primary
                            )
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
                    state.post?.let { post ->
                        PostOptionsContent(
                            onDismiss = { showPostOptionsSheet = false },
                            onReport = { reportTargetId = post.id },
                            isPostOwner = post.userId == state.currentUser?.id,
                            isPostSaved = state.savedPosts.contains(post.id),
                            onSavePost = { onAction(PostDetailAction.OnSavePost(post.id!!)) },
                            onUnsavePost = { onAction(PostDetailAction.OnDeleteSavedPost(post.id!!)) },
                        )
                    }
                }
            }

            if (state.showShareDialog) {
                SharePostDialog(
                    userName = state.currentUser?.fullName ?: "Unknown",
                    userAvatar = state.currentUser?.profileImage ?: "", // Replace with actual avatar if you have it
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

            if(selectedType != null && selectedLikeShowId != null) {
                onAction(PostDetailAction.OnLikesShowClick(selectedLikeShowId!!, selectedType == "post"))
                ModalBottomSheet(
                    onDismissRequest = {
                        selectedLikeShowId = null
                        selectedType != null
                    },
                    sheetState = bottomSheetState
                ) {
                    LikeListContent(
                        listEmotions = state.listLikeUser,
                        onUserClick = { onAction(PostDetailAction.OnUserClick(it)) },
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