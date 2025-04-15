package com.androidfinalproject.hacktok.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.ui.currentProfile.component.StatColumn
import com.androidfinalproject.hacktok.ui.post.component.PostContent
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen (
    state : UserProfileState,
    onAction : (UserProfileAction) -> Unit
) {
    Scaffold(
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
                    containerColor = MaterialTheme.colorScheme.background, // Match background
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
                 // Optional: Add actions like report user?
//                 actions = {
//                    IconButton(onClick = { /* Report action */ }) {
//                        Icon(Icons.Default.Flag, contentDescription = "Report User")
//                    }
//                 }
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
            return@Scaffold // Use return@Scaffold inside Scaffold content
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
                    text = "User not found.", // More specific message
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
            return@Scaffold
        }

        // Main content using LazyColumn for scrollable profile + posts
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background), // Ensure background consistency
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
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp), // Adjusted padding
                        horizontalArrangement = Arrangement.spacedBy(16.dp), // Adjusted spacing
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                             // TODO: Replace with Coil AsyncImage if profileImage is available
                            Text(
                                text = state.user.username?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 32.sp, // Larger text for initial
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        // Stats Column (using separate composable below)
                        ProfileStatsRow(state = state, onAction = onAction)
                    }

                    // Name and Username Column (aligned left)
                    Column(
                       modifier = Modifier
                           .fillMaxWidth()
                           .padding(horizontal = 16.dp),
                       horizontalAlignment = Alignment.Start // Align text left
                    ) {
                         Text(
                            text = state.user.fullName ?: state.user.username ?: "User",
                            style = MaterialTheme.typography.titleLarge, // Use theme typography
                            fontWeight = FontWeight.Bold
                        )
                         Spacer(modifier = Modifier.height(2.dp))
                         Text(
                            text = "@${state.user.username ?: "unknown"}",
                             style = MaterialTheme.typography.bodyMedium, // Use theme typography
                            color = MaterialTheme.colorScheme.outline // Use theme color
                         )
                         Spacer(modifier = Modifier.height(8.dp))
                         Text(
                            state.user.bio ?: "", // Display bio if available
                             style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant, // Use theme color
                            modifier = Modifier.padding(bottom = 16.dp) // Add padding below bio
                         )
                    }


                    // Action Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp) // Add spacing between buttons
                    ) {
                        if (state.isBlocked) {
                            // TODO: Implement Unblock Action
                            Button(
                                onClick = { /* onAction(UserProfileAction.UnblockUser) */ },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Text("Unblock")
                            }
                        } else {
                             if (state.isFriend) {
                                // Friend Actions (Unfriend, Message)
                                Button(
                                    onClick = { onAction(UserProfileAction.Unfriend) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer) // Use container colors
                                ) {
                                    Icon(Icons.Default.PersonRemove, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                    Text("Friends") // Or Unfriend
                                }
                                Button(
                                    onClick = { onAction(UserProfileAction.ChatWithFriend) },
                                    modifier = Modifier.weight(1f)
                                    // Primary color is default
                                ) {
                                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                    Text("Message")
                                }
                             } else {
                                 // Not Friend Action (Add Friend)
                                Button(
                                    onClick = { onAction(UserProfileAction.AddFriend) },
                                    modifier = Modifier.weight(1f)
                                    // Primary color is default
                                ) {
                                     Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                                     Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                    Text("Add Friend")
                                }
                             }
                             // Block Button (Always visible unless already blocked)
                            // Use OutlinedButton for less emphasis
                            OutlinedButton(
                                onClick = { onAction(UserProfileAction.BlockUser) },
                                modifier = Modifier.weight(1f),
                                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp) // Thinner border
                            ) {
                                 Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                                 Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Block")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 0.dp)) // Full width divider
                    // Spacer(modifier = Modifier.height(8.dp)) // Reduced spacer
                }
            }

            // Posts Section Header (Optional)
             item {
                 // Removed explicit "Posts" header, relying on the list content
                 // Spacer(modifier = Modifier.height(8.dp))
             }

            // User's Posts
            if (!state.isBlocked) {
                if (state.posts.isEmpty() && !state.isLoading) { // Check loading state too
                    item {
                         Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                         ) {
                            Icon(Icons.Default.Feed, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "No posts yet.",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                         }
                    }
                } else {
                    items(state.posts, key = { post -> post.id ?: post.hashCode() }) { post ->
                        // Add divider between posts
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 0.dp))
                        PostContent(
                            post = post, // Assumes post.user is populated by ViewModel
                            onPostClick = { postId ->
                                onAction(UserProfileAction.GoToPost(postId))
                            },
                            onToggleLike = {
                                // TODO: Implement isLiked state and pass to PostContent
                                onAction(UserProfileAction.LikePost(post.id ?: ""))
                            },
                            onUserClick = {
                                // Clicking user on their own post - refresh? or do nothing?
                                // onAction(UserProfileAction.RefreshProfile)
                            },
                            onComment = {
                                onAction(UserProfileAction.GoToPost(post.id ?: "")) // Navigate to post detail for commenting
                            },
                            onShare = { /* TODO */ },
                            onOptionsClick = { /* TODO: Show post options */ },
                        )
                    }
                     item { HorizontalDivider() } // Divider after last post
                }
            } else {
                 item { // Show message if user is blocked
                      Column(
                         modifier = Modifier.fillMaxWidth().padding(32.dp),
                         horizontalAlignment = Alignment.CenterHorizontally,
                         verticalArrangement = Arrangement.Center
                      ) {
                         Icon(Icons.Default.Block, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                         Spacer(modifier = Modifier.height(8.dp))
                         Text(
                             "You have blocked this user. Unblock to see their posts.",
                             color = Color.Gray,
                             style = MaterialTheme.typography.bodyMedium,
                             textAlign = androidx.compose.ui.text.style.TextAlign.Center
                         )
                      }
                 }
            }
             // Add padding at the bottom of the list
             item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}


// Extracted stats row for clarity
@Composable
fun ProfileStatsRow(state: UserProfileState, onAction: (UserProfileAction) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly // Distribute space
    ) {
        StatColumn(
             // Use follower/following count if available, otherwise fallback
             count = state.user?.followerCount ?: 0,
             label = "Followers",
             onClick = { state.user?.id?.let { onAction(UserProfileAction.NavigateFriendList(it)) } } // Pass user ID
        )
        StatColumn(
             count = state.user?.followingCount ?: 0,
             label = "Following",
             onClick = { state.user?.id?.let { onAction(UserProfileAction.NavigateFriendList(it)) } } // Pass user ID
        )
        StatColumn(
            count = state.posts.size, // Count loaded posts
            label = "Posts",
            onClick = { /* Maybe scroll to posts? */ }
        )
    }
}


@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun ProfileScreenPreview() {
    MainAppTheme {
         UserProfileScreen(
            state = UserProfileState(
                user = MockData.mockUsers.first().copy(bio = "This is a sample bio text.", followerCount = 123, followingCount = 45),
                posts = MockData.mockPosts.map { it.copy(user = MockData.mockUsers.first()) }, // Ensure user is set for preview
                isBlocked = false,
                isFriend = true, // Preview as friend
                isLoading = false,
                error = null
            ),
            onAction = {}
         )
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun ProfileScreenNotFriendPreview() {
    MainAppTheme {
         UserProfileScreen(
            state = UserProfileState(
                user = MockData.mockUsers.first().copy(followerCount = 5, followingCount = 10),
                posts = MockData.mockPosts.take(1).map { it.copy(user = MockData.mockUsers.first()) },
                isBlocked = false,
                isFriend = false, // Preview as not friend
                isLoading = false,
                error = null
            ),
            onAction = {}
         )
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun ProfileScreenBlockedPreview() {
    MainAppTheme {
         UserProfileScreen(
            state = UserProfileState(
                user = MockData.mockUsers.first(),
                posts = emptyList(), // No posts shown when blocked
                isBlocked = true, // Preview as blocked
                isFriend = false,
                isLoading = false,
                error = null
            ),
            onAction = {}
         )
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun ProfileScreenNoPostsPreview() {
    MainAppTheme {
         UserProfileScreen(
            state = UserProfileState(
                user = MockData.mockUsers.first().copy(followerCount = 99, followingCount = 1),
                posts = emptyList(), // Empty post list
                isBlocked = false,
                isFriend = true,
                isLoading = false,
                error = null
            ),
            onAction = {}
         )
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun ProfileScreenErrorPreview() {
    MainAppTheme {
         UserProfileScreen(
            state = UserProfileState(
                user = null,
                posts = emptyList(),
                isBlocked = false,
                isFriend = false,
                isLoading = false,
                error = "Failed to load profile: Network Error 404" // Example error
            ),
            onAction = {}
         )
    }
}