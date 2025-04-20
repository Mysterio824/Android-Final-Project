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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.R
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.enums.ReportType
import com.androidfinalproject.hacktok.ui.mainDashboard.home.component.*
import com.androidfinalproject.hacktok.ui.commonComponent.PostContent
import com.androidfinalproject.hacktok.ui.commonComponent.PostOptionsContent
import com.androidfinalproject.hacktok.ui.commonComponent.ReportOptionsContent
import com.androidfinalproject.hacktok.ui.commonComponent.SharePostDialog
import com.androidfinalproject.hacktok.ui.post.PostDetailAction
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeScreenState,
    onAction: (HomeScreenAction) -> Unit,
) {
    val facebookBlue = Color(0xFF1877F2)
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

                        items(state.posts) { post ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RectangleShape,
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                PostContent(
                                    post = post,
                                    onPostClick = { onAction(HomeScreenAction.OnPostClick(post.id!!)) },
                                    onToggleLike = { onAction(HomeScreenAction.LikePost(post.id!!)) },
                                    onUnLike = { onAction(HomeScreenAction.UnLikePost(post.id!!)) },
                                    onUserClick = { onAction(HomeScreenAction.OnUserClick(post.userId)) },
                                    onComment = { onAction(HomeScreenAction.OnPostClick(post.id!!)) },
                                    onShare = { onAction(HomeScreenAction.UpdateSharePost(post)) },
                                    onOptionsClick = { selectPostId = post.id },
                                    currentId = state.user?.id ?: ""
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    if (state.showShareDialog) {
                        SharePostDialog(
                            userName = state.user?.fullName ?: "Unknown",
                            userAvatar = painterResource(id = R.drawable.profile_placeholder), // Replace with actual avatar if you have it
                            onDismiss = { onAction(HomeScreenAction.DismissShareDialog) },
                            onSubmit = { caption, privacy ->
                                onAction(HomeScreenAction.OnSharePost(post = state.sharePost!!, caption = caption, privacy = privacy))
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

            if (selectPostId != null) {
                ModalBottomSheet(
                    onDismissRequest = { selectPostId = null },
                    sheetState = bottomSheetState
                ) {
                    PostOptionsContent(
                        onDismiss = { selectPostId = null },
                        onReport = { reportTargetId = selectPostId!! },
                        isPostOwner = state.user?.id == selectPostId
                    )
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
            HomeScreen(
                state = HomeScreenState(
                    posts = MockData.mockPosts,
                ),
                onAction = {},
            )
        }
    }
}