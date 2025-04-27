package com.androidfinalproject.hacktok.ui.mainDashboard.watchLater

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.enums.ReportType
import com.androidfinalproject.hacktok.ui.commonComponent.PostContent
import com.androidfinalproject.hacktok.ui.commonComponent.PostOptionsContent
import com.androidfinalproject.hacktok.ui.commonComponent.ReportOptionsContent
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchLaterScreen(
    state: WatchLaterState,
    onAction: (WatchLaterAction) -> Unit
) {
    var reportTargetId by remember { mutableStateOf<String?>(null) }
    var selectPostId by remember { mutableStateOf<String?>(null) }
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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        when {
            state.isLoading -> {
                CircularProgressIndicator()
            }
            state.error != null -> {
                Text(text = state.error)
            }
            state.savedPosts.isEmpty() -> {
                Text(text = "No saved posts")
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.padding(paddingValues)
                ){
                    items(state.savedPosts) { post ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RectangleShape
                        ) {
                            PostContent(
                                post = post,

                                onPostClick = {
                                    onAction(WatchLaterAction.OnPostClick(post.id!!))
                                },
                                onToggleLike = {
                                    onAction(WatchLaterAction.OnLikeClick(post.id!!))
                                },
                                onUserClick = {
                                    onAction(WatchLaterAction.OnUserClick(post.userId))
                                },
                                onComment = {
                                     onAction(WatchLaterAction.OnCommentClick(post.id!!))
                                },
                                onShare = {},
                                onOptionsClick = { selectPostId = post.id },
                                onUnLike = { onAction(WatchLaterAction.OnUnLikeClick(post.id!!)) },
                                currentId = state.currentUserId ?: "",
                                user = User(),
                            )
                        }
                    }
                }
                if (reportTargetId != null) {
                    ModalBottomSheet(
                        onDismissRequest = { reportTargetId = null },
                        sheetState = bottomSheetState
                    ) {
                        ReportOptionsContent(
                            onDismiss = { reportTargetId = null },
                            targetId = reportTargetId!!,
                            onReportCauseSelected = { id, cause, type ->
                                onAction(WatchLaterAction.SubmitReport(id, type, cause))
                            },
                            type = ReportType.Post,
                        )
                    }
                }

                if (selectPostId != null) {
                    ModalBottomSheet(
                        onDismissRequest = { selectPostId = null },
                        sheetState = bottomSheetState
                    ) {
                        PostOptionsContent(
                            onDismiss = { selectPostId = null },
                            onReport = { reportTargetId = selectPostId!! },
                            isPostOwner = state.currentUserId == selectPostId
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WatchLaterScreenPreview() {
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            WatchLaterScreen(
                state = WatchLaterState(
                    savedPosts = MockData.mockPosts
                ),
                onAction = {}
            )
        }
    }
}