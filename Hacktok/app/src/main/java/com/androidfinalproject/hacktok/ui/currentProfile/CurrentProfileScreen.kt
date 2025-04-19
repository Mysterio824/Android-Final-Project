package com.androidfinalproject.hacktok.ui.currentProfile

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.R
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.commonComponent.PostContent
import com.androidfinalproject.hacktok.ui.commonComponent.ProfileImage
import com.androidfinalproject.hacktok.ui.commonComponent.PostOptionsContent
import com.androidfinalproject.hacktok.ui.commonComponent.SharePostDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentProfileScreen(
    state: CurrentProfileState,
    onAction: (CurrentProfileAction) -> Unit
) {

    var selectPostId by remember { mutableStateOf<String?>(null) }

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = { onAction(CurrentProfileAction.OnNavigateBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onAction(CurrentProfileAction.NavigateToProfileEdit) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAction(CurrentProfileAction.NavigateToNewPost) }) {
                Icon(Icons.Default.Add, contentDescription = "Create Post")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is CurrentProfileState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is CurrentProfileState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Error: ${(state).message}",
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { onAction(CurrentProfileAction.RetryLoading) }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                is CurrentProfileState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        item {
                            ProfileHeader(
                                user = state.user,
                                friendCount = state.friendCount,
                                onFriendListCLick = { onAction(CurrentProfileAction.NavigateFriendList(state.user.id!!)) }
                            )
                        }
                        items(state.posts.filter { !it.id.isNullOrBlank() }) { post ->
                            PostContent(
                                post = post,
                                fullName = state.user.fullName,
                                onPostClick = { onAction(CurrentProfileAction.OnPostClick(post)) },
                                onToggleLike = { /* Handle like toggle */ },
                                onComment = { onAction(CurrentProfileAction.OnPostClick(post)) },
                                onShare = {
                                    onAction(CurrentProfileAction.UpdateSharePost(post))
                                },
                                onOptionsClick = { selectPostId = post.id },
                                onUserClick = { onAction(CurrentProfileAction.OnUserClick(post.userId)) },
                            )
                        }
                    }

                    if (state.showShareDialog && state.postToShare != null) {
                        SharePostDialog(
                            userName = state.user.fullName ?: "Unknown",
                            userAvatar = painterResource(id = R.drawable.profile_placeholder), // Replace with actual avatar if you have it
                            onDismiss = { onAction(CurrentProfileAction.DismissShareDialog) },
                            onSubmit = { caption, privacy ->
                                // Handle the share logic here, e.g., call onAction
                                onAction(CurrentProfileAction.OnSharePost(post = state.postToShare, caption = caption, privacy = privacy))
                                onAction(CurrentProfileAction.DismissShareDialog)
                            }
                        )
                    }
                }
            }
        }



        if (selectPostId != null) {
            ModalBottomSheet(
                onDismissRequest = { selectPostId = null },
                sheetState = bottomSheetState
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

@Composable
private fun ProfileHeader(
    user: User,
    friendCount: Int,
    onFriendListCLick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image
        ProfileImage(
            imageUrl = user.profileImage,
            size = 120.dp,
            onClick = {}
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Username
        Text(
            text = user.username ?: "Unknown User",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Friend Count
        Text(
            text = "$friendCount Friends",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.clickable { onFriendListCLick() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bio
        Text(
            text = user.bio ?: "No bio available",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}