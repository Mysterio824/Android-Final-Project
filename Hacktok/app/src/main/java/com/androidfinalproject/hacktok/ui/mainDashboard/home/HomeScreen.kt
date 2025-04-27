package com.androidfinalproject.hacktok.ui.mainDashboard.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.Ad
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.PrivacySettings
import com.androidfinalproject.hacktok.model.TargetAudience
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.enums.ReportType
import com.androidfinalproject.hacktok.model.enums.UserRole
import com.androidfinalproject.hacktok.ui.commonComponent.LikeListContent
import com.androidfinalproject.hacktok.ui.mainDashboard.home.component.*
import com.androidfinalproject.hacktok.ui.commonComponent.PostContent
import com.androidfinalproject.hacktok.ui.commonComponent.PostOptionsContent
import com.androidfinalproject.hacktok.ui.commonComponent.ReportOptionsContent
import com.androidfinalproject.hacktok.ui.commonComponent.SharePostDialog
import com.androidfinalproject.hacktok.ui.profile.UserProfileAction
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeScreenState,
    onAction: (HomeScreenAction) -> Unit,
) {
    val facebookBlue = Color(0xFF1877F2)
    var reportTargetId by remember { mutableStateOf<String?>(null) }
    var selectPost by remember { mutableStateOf<Post?>(null) }
    var selectedLikeShowId by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

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

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                val totalItemsCount = listState.layoutInfo.totalItemsCount
                if (lastVisibleItemIndex == totalItemsCount - 1 &&
                    !state.isPaginating &&
                    state.hasMorePosts
                ) {
                    onAction(HomeScreenAction.LoadMorePosts)
                }
            }
    }

    val isRefreshing = state.isLoading

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = {
            onAction(HomeScreenAction.Refresh)
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF0F2F5))
            ) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = facebookBlue
                        )
                    }

                    state.error != null -> {
                        Text(
                            text = state.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    state.posts.isEmpty() -> {
                        Text(
                            text = "No posts available",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    else -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            item {
                                WhatsNewBar(
                                    profilePicUrl = state.user?.profileImage ?: "",
                                    onNewPostCLick = { onAction(HomeScreenAction.OnCreatePost) }
                                )
                            }

                            item {
                                StoriesSection(
                                    stories = state.stories,
                                    onCreateStory = { onAction(HomeScreenAction.OnCreateStory) },
                                    onStoryClick = { onAction(HomeScreenAction.OnStoryClick(it)) }
                                )
                            }

                            // Display ad if available
                            state.currentAd?.let { ad ->
                                item {
                                    AdContent(
                                        ad = ad,
                                        onAdClick = { onAction(HomeScreenAction.OnAdClick) },
                                        onInterested = { onAction(HomeScreenAction.OnAdInterested) },
                                        onUninterested = { onAction(HomeScreenAction.OnAdUninterested) },
                                        currentUserId = state.user?.id ?: ""
                                    )
                                }
                            }

                            items(state.posts) { post ->
                                val refPost = state.referencePosts[post.refPostId]
                                val refUser = refPost?.userId?.let { state.referenceUsers[it] }
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RectangleShape,
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    PostContent(
                                        post = post,
                                        user = state.postUsers[post.id]!!,
                                        referencePost = refPost,
                                        referenceUser = refUser,
                                        onPostClick = { onAction(HomeScreenAction.OnPostClick(post.id!!)) },
                                        onToggleLike = { onAction(HomeScreenAction.LikePost(post.id!!, it)) },
                                        onUnLike = { onAction(HomeScreenAction.UnLikePost(post.id!!)) },
                                        onUserClick = { onAction(HomeScreenAction.OnUserClick(post.userId)) },
                                        onComment = { onAction(HomeScreenAction.OnPostClick(post.id!!)) },
                                        onShare = { onAction(HomeScreenAction.UpdateSharePost(post)) },
                                        onOptionsClick = { selectPost = post },
                                        currentId = state.user?.id ?: "",
                                        onLikesClick = { selectedLikeShowId = post.id },
                                        onImageClick = { onAction(HomeScreenAction.OnImageClick(it)) }
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            if (state.isPaginating) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = facebookBlue)
                                    }
                                }
                            }
                        }

                        if (state.showShareDialog) {
                            SharePostDialog(
                                userName = state.user?.fullName ?: "Unknown",
                                userAvatar = state.user?.profileImage
                                    ?: "",
                                onDismiss = { onAction(HomeScreenAction.DismissShareDialog) },
                                onSubmit = { caption, privacy ->
                                    onAction(
                                        HomeScreenAction.OnSharePost(
                                            post = state.sharePost!!,
                                            caption = caption,
                                            privacy = privacy
                                        )
                                    )
                                    onAction(HomeScreenAction.DismissShareDialog)
                                }
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
                                onAction(HomeScreenAction.SubmitReport(id, type, cause))
                            },
                            type = ReportType.Post,
                        )
                    }
                }

                if (selectPost != null) {
                    ModalBottomSheet(
                        onDismissRequest = { selectPost = null },
                        sheetState = bottomSheetState
                    ) {
                        PostOptionsContent(
                            onDismiss = { selectPost = null },
                            onReport = { reportTargetId = selectPost!!.id },
                            onSavePost = { onAction(HomeScreenAction.OnSavePost(selectPost!!.id!!)) },
                            onPostEdit = { onAction(HomeScreenAction.OnPostEditClick(selectPost!!.id!!)) },
                            onPostDelete = { onAction(HomeScreenAction.DeletePost(selectPost!!.id!!)) },
                            isPostOwner = state.user!!.id == selectPost!!.userId
                        )
                    }
                }

                if(selectedLikeShowId != null) {
                    onAction(HomeScreenAction.OnLikesShowClick(selectedLikeShowId!!))
                    ModalBottomSheet(
                        onDismissRequest = { selectedLikeShowId = null },
                        sheetState = bottomSheetState,
                    ) {
                        LikeListContent(
                            listEmotions = state.listLikeUser,
                            onUserClick = { onAction(HomeScreenAction.OnUserClick(it)) },
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            // Create a mock ad
            val mockAd = Ad(
                id = "preview_ad_1",
                advertiserId = "advertiser_1",
                userId = "user_1",
                content = "Check out our amazing products! Limited time offer.",
                mediaUrl = "https://res.cloudinary.com/dbeximude/image/upload/v1744990649/yhfww12fm92fipcxgz06.jpg",
                targetAudience = TargetAudience(),
                impressions = 100,
                clicks = 25,
                createdAt = Date(),
                endDate = Date(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L))
            )

            // Create a mock user
            val mockUser = User(
                id = "user_1",
                username = "johndoe",
                email = "john@example.com",
                fullName = "John Doe",
                profileImage = "https://picsum.photos/200",
                createdAt = Date(),
                isActive = true,
                role = UserRole.USER,
                bio = "Hello, I'm John!",
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
            )

            HomeScreen(
                state = HomeScreenState(
                    posts = MockData.mockPosts,
                    currentAd = mockAd,
                    user = mockUser,
                    postAuthorNames = mapOf(
                        "1" to "John Doe",
                        "2" to "Jane Smith",
                        "3" to "Mike Johnson"
                    )
                ),
                onAction = {}
            )
        }
    }
}