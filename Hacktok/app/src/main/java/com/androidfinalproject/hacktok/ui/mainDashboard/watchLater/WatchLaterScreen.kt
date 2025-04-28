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
import com.androidfinalproject.hacktok.model.enums.ReportType
import com.androidfinalproject.hacktok.ui.commonComponent.LikeListContent
import com.androidfinalproject.hacktok.ui.commonComponent.PostContent
import com.androidfinalproject.hacktok.ui.commonComponent.PostOptionsContent
import com.androidfinalproject.hacktok.ui.commonComponent.ReportOptionsContent
import com.androidfinalproject.hacktok.ui.commonComponent.SharePostDialog
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.enums.UserRole
import com.androidfinalproject.hacktok.model.PrivacySettings
import com.androidfinalproject.hacktok.ui.mainDashboard.home.HomeScreenAction
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchLaterScreen(
    state: WatchLaterState,
    onAction: (WatchLaterAction) -> Unit
) {
    var reportTargetId by remember { mutableStateOf<String?>(null) }
    var selectPostId by remember { mutableStateOf<String?>(null) }
    var selectedLikeShowId by remember { mutableStateOf<String?>(null) }
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
    val isRefreshing = state.isLoading

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = {
            onAction(WatchLaterAction.Refresh)
        }
    ) {

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
                    ) {
                        items(state.posts) { post ->
                            val refPost = state.referencePosts[post.refPostId]
                            val refUser = refPost?.userId?.let { state.referenceUsers[it] }
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
                                        onAction(WatchLaterAction.OnLikeClick(post.id!!, it))
                                    },
                                    onUserClick = {
                                        onAction(WatchLaterAction.OnUserClick(post.userId))
                                    },
                                    onComment = {
                                        onAction(WatchLaterAction.OnPostClick(post.id!!))
                                    },
                                    onShare = {
                                        onAction(WatchLaterAction.UpdateSharePost(post))
                                    },
                                    onOptionsClick = { selectPostId = post.id },
                                    onUnLike = { onAction(WatchLaterAction.OnUnLikeClick(post.id!!)) },
                                    currentId = state.currentUserId ?: "",
                                    user = state.postUsers[post.id] ?: state.user ?: User(
                                        id = post.userId,
                                        email = "",
                                        username = "Unknown User",
                                        fullName = "Unknown User",
                                        profileImage = null,
                                        bio = null,
                                        createdAt = Date(),
                                        isActive = true,
                                        role = UserRole.USER,
                                        privacySettings = PrivacySettings(),
                                        language = "en",
                                        friends = emptyList(),
                                        blockedUsers = emptyList(),
                                        followers = emptyList(),
                                        following = emptyList(),
                                        followerCount = 0,
                                        followingCount = 0,
                                        searchHistory = emptyList(),
                                        videosCount = 0
                                    ),
                                    onImageClick = {
                                        onAction(WatchLaterAction.OnImageClick(post.imageLink))
                                    },
                                    onLikesClick = {
                                        onAction(WatchLaterAction.OnLikesShowClick(post.id!!))
                                    },
                                    referencePost = refPost,
                                    referenceUser = refUser,
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
                                onReport = { reportTargetId = selectPostId },
                                isPostOwner = selectPostId == state.user?.id,
                                isPostSaved = state.savedPosts.contains(selectPostId),
                                onSavePost = {},
                                onUnsavePost = {
                                    onAction(
                                        WatchLaterAction.OnDeleteSavedPost(
                                            selectPostId!!
                                        )
                                    )
                                },
                                onPostEdit = {
                                    onAction(
                                        WatchLaterAction.OnPostEditClick(
                                            selectPostId!!
                                        )
                                    )
                                },
                                onPostDelete = { onAction(WatchLaterAction.OnDeletePost(selectPostId!!)) }
                            )
                        }
                    }

                    if (selectedLikeShowId != null) {
                        onAction(WatchLaterAction.OnLikesShowClick(selectedLikeShowId!!))
                        ModalBottomSheet(
                            onDismissRequest = { selectedLikeShowId = null },
                            sheetState = bottomSheetState,
                        ) {
                            LikeListContent(
                                listEmotions = state.listLikeUser,
                                onUserClick = { onAction(WatchLaterAction.OnUserClick(it)) },
                            )
                        }
                    }

                    if (state.showShareDialog) {
                        SharePostDialog(
                            userName = state.user?.fullName ?: "Unknown",
                            userAvatar = state.user?.profileImage
                                ?: "",
                            onDismiss = { onAction(WatchLaterAction.DismissShareDialog) },
                            onSubmit = { caption, privacy ->
                                onAction(
                                    WatchLaterAction.OnSharePost(
                                        post = state.sharePost!!,
                                        caption = caption,
                                        privacy = privacy
                                    )
                                )
                                onAction(WatchLaterAction.DismissShareDialog)
                            }
                        )
                    }
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun WatchLaterScreenPreview() {
//    MainAppTheme {
//        Box(
//            modifier = Modifier
//                .width(400.dp)
//                .height(800.dp)
//        ) {
//            WatchLaterScreen(
//                state = WatchLaterState(
//                    currentUserId = "user1",
//                    postUsers = mapOf(
//                        "post1" to MockData.mockUsers[0],
//                        "post2" to MockData.mockUsers[1]
//                    ),
//                    savedPosts = MockData.mockPosts,
//                    referencePosts = mapOf(
//                        "ref1" to MockData.mockPosts[0],
//                        "ref2" to MockData.mockPosts[1]
//                    ),
//                    referenceUsers = mapOf(
//                        "user1" to MockData.mockUsers[0],
//                        "user2" to MockData.mockUsers[1]
//                    ),
//                    user = MockData.mockUsers[0]
//                ),
//                onAction = {}
//            )
//        }
//    }
//}