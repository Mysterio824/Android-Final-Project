package com.androidfinalproject.hacktok.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.RelationInfo
import com.androidfinalproject.hacktok.model.enums.RelationshipStatus
import com.androidfinalproject.hacktok.model.enums.ReportType
import com.androidfinalproject.hacktok.ui.commonComponent.LikeListContent
import com.androidfinalproject.hacktok.ui.commonComponent.PostContent
import com.androidfinalproject.hacktok.ui.commonComponent.PostOptionsContent
import com.androidfinalproject.hacktok.ui.commonComponent.ProfileImage
import com.androidfinalproject.hacktok.ui.commonComponent.ReportOptionsContent
import com.androidfinalproject.hacktok.ui.commonComponent.SharePostDialog
import com.androidfinalproject.hacktok.ui.currentProfile.CurrentProfileScreen
import com.androidfinalproject.hacktok.ui.post.PostDetailAction
import com.androidfinalproject.hacktok.ui.profile.component.*
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen (
    state : UserProfileState,
    onAction : (UserProfileAction) -> Unit
) {
    var showProfileOptionsSheet by remember { mutableStateOf(false) }
    var showResponseOptionsSheet by remember { mutableStateOf(false) }
    var reportTargetId by remember { mutableStateOf<String?>(null) }
    var selectPostId by remember { mutableStateOf<String?>(null) }
    var selectedLikeShowId by remember { mutableStateOf<String?>(null) }
    var reportType by remember { mutableStateOf<ReportType?>(null) }
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
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(state.user?.username ?: "Profile") },
                navigationIcon = {
                    IconButton(onClick = { onAction(UserProfileAction.NavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (state.error != null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                 Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(onClick = { onAction(UserProfileAction.RefreshProfile) }) {
                         Text("Retry")
                    }
                 }
            }
            return@Scaffold
        }

        if (state.user == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "User not found.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
            return@Scaffold
        }

        if (state.showShareDialog) {
            SharePostDialog(
                userName = state.currentUser?.fullName ?: "Unknown",
                userAvatar = state.currentUser?.profileImage ?: "", // Replace with actual avatar if you have it
                onDismiss = { onAction(UserProfileAction.DismissShareDialog) },
                onSubmit = { caption, privacy ->
                    onAction(UserProfileAction.OnSharePost(post = state.sharePost!!, caption = caption, privacy = privacy))
                    onAction(UserProfileAction.DismissShareDialog)
                }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Header Item
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Picture Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ProfileImage(
                            imageUrl = state.user.profileImage,
                            size = 80.dp,
                            onClick = { onAction(UserProfileAction.RefreshProfile) }
                        )
                        ProfileStatsRow(state = state, onAction = onAction)
                    }

                    // Name and Username Column
                    Column(
                       modifier = Modifier
                           .fillMaxWidth()
                           .padding(horizontal = 16.dp),
                       horizontalAlignment = Alignment.Start
                    ) {
                         Text(
                            text = state.user.fullName ?: state.user.username ?: "User",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                         Spacer(modifier = Modifier.height(2.dp))
                         Text(
                            text = "@${state.user.username ?: "unknown"}",
                             style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                         )
                         Spacer(modifier = Modifier.height(8.dp))
                         Text(
                            state.user.bio ?: "",
                             style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                         )
                    }

                    // Action Buttons Row (Only show if not own profile)
                    if (state.currentUser?.id != state.user.id) {
                        ProfileActionButtons(
                            state = state,
                            onAction = onAction,
                            showOption = { showProfileOptionsSheet = true },
                            showResponse = { showResponseOptionsSheet = true},
                            isOwnProfile = false
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 0.dp))
                }
            }

            // Posts Section
            // Hide posts if current user is blocked by profile user
            val showPosts = state.relationshipInfo?.status != RelationshipStatus.BLOCKING
            
            if (showPosts) {
                 if (state.posts.isEmpty()) {
                     item {
                         Column(
                             modifier = Modifier.fillMaxWidth().padding(32.dp),
                             horizontalAlignment = Alignment.CenterHorizontally,
                             verticalArrangement = Arrangement.Center
                         ) {
                             Icon(
                                 imageVector = Icons.Default.CameraRoll,
                                 contentDescription = "No Posts",
                                 modifier = Modifier.size(48.dp),
                                 tint = MaterialTheme.colorScheme.onSurfaceVariant
                             )
                             Spacer(modifier = Modifier.height(8.dp))
                             Text("No posts yet", style = MaterialTheme.typography.titleMedium)
                         }
                     }
                 } else {
                     items(state.posts, key = { it.id ?: "" }) {
                         val referencePost = state.referencePosts[it.refPostId]
                         val referenceUser = referencePost?.userId?.let { refUserId ->
                             state.referenceUsers[refUserId]
                         }
                         PostContent(
                             post = it,
                             onUserClick = { onAction(UserProfileAction.RefreshProfile) },
                             onPostClick = { postId -> onAction(UserProfileAction.GoToPost(postId)) },
                             onOptionsClick = { selectPostId = it.id },
                             onToggleLike = { onAction(UserProfileAction.LikePost(it.id!!)) },
                             onComment = { onAction(UserProfileAction.GoToPost(it.id!!)) },
                             onShare = { onAction(UserProfileAction.UpdateSharePost(it)) },
                             onUnLike = { onAction(UserProfileAction.UnlikePost(it.id!!)) },
                             currentId = state.currentUser?.id ?: "",
                             onLikesClick = { postId -> selectedLikeShowId = postId },
                             user = state.user,
                             referenceUser = referenceUser,
                             referencePost = referencePost
                         )
                     }
                 }
            } else {
                // Show a "Blocked" indicator instead of posts
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = "Blocked",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("You have blocked this user", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }

        if (showProfileOptionsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showProfileOptionsSheet = false },
                sheetState = bottomSheetState
            ) {
                ProfileOptionsContent(
                    onDismiss = { showProfileOptionsSheet = false },
                    report = {
                        reportTargetId = state.user.id
                        reportType = ReportType.User
                    },
                    seeFriend = { onAction(UserProfileAction.NavigateFriendList) },
                    block = { onAction(UserProfileAction.BlockUser) },
                    unblock = { onAction(UserProfileAction.UnblockUser) },
                    isBlock = state.relationshipInfo?.status == RelationshipStatus.BLOCKING
                )
            }
        }

        if (showResponseOptionsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showResponseOptionsSheet = false },
                sheetState = bottomSheetState
            ) {
                ResponseOptionsContent(
                    onDismiss = { showResponseOptionsSheet = false },
                    accept = { onAction(UserProfileAction.AcceptFriendRequest) },
                    unaccepted = { onAction(UserProfileAction.DeclineFriendRequest) }
                )
            }
        }

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
                        onAction(UserProfileAction.SubmitReport(id, type, cause))
                    },
                    type = reportType!!,
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
                    isPostOwner = false
                )
            }
        }

        if(selectedLikeShowId != null) {
            onAction(UserProfileAction.OnLikesShowClick(selectedLikeShowId!!))
            ModalBottomSheet(
                onDismissRequest = { selectedLikeShowId = null },
                sheetState = bottomSheetState
            ) {
                LikeListContent(
                    users = state.listLikeUser,
                    onUserClick = { onAction(UserProfileAction.OnUserClick(it)) },
                    onDismiss = { selectedLikeShowId = null }
                )
            }
        }
    }
}