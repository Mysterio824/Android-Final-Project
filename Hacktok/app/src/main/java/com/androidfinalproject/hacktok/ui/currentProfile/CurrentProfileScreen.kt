package com.androidfinalproject.hacktok.ui.currentProfile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.style.TextAlign
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.commonComponent.LikeListContent
import com.androidfinalproject.hacktok.ui.commonComponent.PostContent
import com.androidfinalproject.hacktok.ui.commonComponent.PostOptionsContent
import com.androidfinalproject.hacktok.ui.commonComponent.ProfileImage
import com.androidfinalproject.hacktok.ui.commonComponent.SharePostDialog
import com.androidfinalproject.hacktok.ui.mainDashboard.home.HomeScreenAction
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentProfileScreen(
    state: CurrentProfileState,
    onAction: (CurrentProfileAction) -> Unit
) {
    var selectPostId by remember { mutableStateOf<String?>(null) }
    var selectedLikeShowId by remember { mutableStateOf<String?>(null) }

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )
    val isRefreshing = state == CurrentProfileState.Loading

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = {
            onAction(CurrentProfileAction.Refresh)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Profile",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { onAction(CurrentProfileAction.OnNavigateBack) }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { onAction(CurrentProfileAction.NavigateToProfileEdit) }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { onAction(CurrentProfileAction.NavigateToNewPost) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Post")
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = 0.dp,
                        start = paddingValues.calculateLeftPadding(layoutDirection = LayoutDirection.Ltr),
                        end = paddingValues.calculateRightPadding(layoutDirection = LayoutDirection.Ltr),
                        bottom = paddingValues.calculateBottomPadding()
                    )
            ) {
                when (state) {
                    is CurrentProfileState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 3.dp
                            )
                        }
                    }

                    is CurrentProfileState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Error: ${(state).message}",
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { onAction(CurrentProfileAction.RetryLoading) },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Retry")
                                }
                            }
                        }
                    }

                    is CurrentProfileState.Success -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(top = paddingValues.calculateTopPadding())
                        ) {
                            item {
                                ProfileHeader(
                                    user = state.user,
                                    friendCount = state.friendCount,
                                    onFriendListClick = {
                                        onAction(
                                            CurrentProfileAction.NavigateFriendList(
                                                state.user.id!!
                                            )
                                        )
                                    }
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            items(state.posts.filter { !it.id.isNullOrBlank() }) { post ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                            alpha = 0.3f
                                        )
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                ) {
                                    PostContent(
                                        post = post,
                                        fullName = state.user.fullName,
                                        onPostClick = {
                                            onAction(
                                                CurrentProfileAction.OnPostClick(
                                                    post
                                                )
                                            )
                                        },
                                        onToggleLike = {
                                            onAction(
                                                CurrentProfileAction.OnLike(
                                                    post.id!!,
                                                    it,
                                                    true
                                                )
                                            )
                                        },
                                        onUnLike = {
                                            onAction(
                                                CurrentProfileAction.OnLike(
                                                    post.id!!,
                                                    "",
                                                    false
                                                )
                                            )
                                        },
                                        onComment = { onAction(CurrentProfileAction.OnPostClick(post)) },
                                        onShare = {
                                            onAction(CurrentProfileAction.UpdateSharePost(post))
                                        },
                                        onOptionsClick = { selectPostId = post.id },
                                        onUserClick = {
                                            onAction(
                                                CurrentProfileAction.OnUserClick(
                                                    post.userId
                                                )
                                            )
                                        },
                                        onLikesClick = { postId -> selectedLikeShowId = postId },
                                        currentId = state.user.id ?: ""
                                    )
                                }
                            }
                        }

                        if (state.showShareDialog && state.postToShare != null) {
                            SharePostDialog(
                                userName = state.user.fullName ?: "Unknown",
                                userAvatar = state.user.profileImage ?: "",
                                onDismiss = { onAction(CurrentProfileAction.DismissShareDialog) },
                                onSubmit = { caption, privacy ->
                                    onAction(
                                        CurrentProfileAction.OnSharePost(
                                            post = state.postToShare,
                                            caption = caption,
                                            privacy = privacy
                                        )
                                    )
                                    onAction(CurrentProfileAction.DismissShareDialog)
                                }
                            )
                        }
                        if(selectedLikeShowId != null) {
                            onAction(CurrentProfileAction.OnLikesShowClick(selectedLikeShowId!!))
                            ModalBottomSheet(
                                onDismissRequest = { selectedLikeShowId = null },
                                sheetState = bottomSheetState
                            ) {
                                LikeListContent(
                                    listEmotions = state.listLikeUser,
                                    onUserClick = { onAction(CurrentProfileAction.OnUserClick(it)) },
                                    onDismiss = { selectedLikeShowId = null }
                                )
                            }
                        }
                    }
                }
            }

            if (selectPostId != null) {
                ModalBottomSheet(
                    onDismissRequest = { selectPostId = null },
                    sheetState = bottomSheetState,
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    PostOptionsContent(
                        onDismiss = { selectPostId = null },
                        onReport = {},
                        isPostOwner = true,
                        onPostEdit = { onAction(CurrentProfileAction.NavigateToPostEdit(selectPostId!!)) },
                        onPostDelete = { onAction(CurrentProfileAction.OnDeletePost(selectPostId!!)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    user: User,
    friendCount: Int,
    onFriendListClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image with soft shadow and larger size
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(percent = 50))
                .background(MaterialTheme.colorScheme.surface)
                .padding(4.dp)
        ) {
            ProfileImage(
                imageUrl = user.profileImage,
                size = 140.dp,
                onClick = {}
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Username with enhanced typography
        Text(
            text = user.username ?: "Unknown User",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Full name or secondary info with subtle color
        user.fullName?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Friend List button in a nice pill shape
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .clickable { onFriendListClick() }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.People,
                    contentDescription = "Friends",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Friend List",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bio with better styling
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Bio",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = user.bio ?: "No bio available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}