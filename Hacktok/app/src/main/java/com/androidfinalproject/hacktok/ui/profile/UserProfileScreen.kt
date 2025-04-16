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
import com.androidfinalproject.hacktok.model.RelationInfo
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.enums.RelationshipStatus
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
        
        val isOwnProfile = state.currentUserId == state.user.id

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
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                             // TODO: Replace with Coil AsyncImage
                            Text(
                                text = state.user.username?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
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
                    if (!isOwnProfile) {
                        ProfileActionButtons(state = state, onAction = onAction)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 0.dp))
                }
            }

            // Posts Section
            // Hide posts if current user is blocked by profile user
            val showPosts = state.relationshipInfo?.status != RelationshipStatus.BLOCKING
            
            if (showPosts) {
                 if (state.posts.isEmpty() && !state.isLoading) { 
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
                         PostContent(
                             post = it,
                             onUserClick = { onAction(UserProfileAction.RefreshProfile) },
                             onPostClick = { onAction(UserProfileAction.GoToPost(it.id!!)) },
                             onOptionsClick = { /* TODO: Post options */ },
                             onToggleLike = { onAction(UserProfileAction.LikePost(it.id!!)) },
                             onComment = { onAction(UserProfileAction.GoToPost(it.id!!)) },
                             onShare = { /* TODO: Share post */ }
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
    }
}

// Extracted Action Buttons to a separate composable for clarity
@Composable
private fun ProfileActionButtons(state: UserProfileState, onAction: (UserProfileAction) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (state.relationshipInfo?.status) {
            RelationshipStatus.NONE -> {
                // Can Send Request
                Button(
                    onClick = { onAction(UserProfileAction.SendFriendRequest) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Add Friend")
                }
                BlockButton(modifier = Modifier.weight(1f), onAction = onAction)
            }
            RelationshipStatus.PENDING_OUTGOING -> {
                // Request Sent by Current User
                Button(
                    onClick = { onAction(UserProfileAction.CancelFriendRequest) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                ) {
                    Icon(Icons.Default.CancelScheduleSend, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Cancel Request")
                }
                 BlockButton(modifier = Modifier.weight(1f), onAction = onAction)
            }
            RelationshipStatus.PENDING_INCOMING -> {
                 // Request Received by Current User
                 Button(
                     onClick = { onAction(UserProfileAction.AcceptFriendRequest) },
                     modifier = Modifier.weight(1f),
                     colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                 ) {
                     Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                     Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                     Text("Accept")
                 }
                 Button(
                     onClick = { onAction(UserProfileAction.DeclineFriendRequest) },
                     modifier = Modifier.weight(1f),
                      colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                 ) {
                     Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                     Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                     Text("Decline")
                 }
            }
            RelationshipStatus.FRIENDS -> {
                 // Are Friends
                 Button(
                     onClick = { onAction(UserProfileAction.Unfriend) },
                     modifier = Modifier.weight(1f),
                     colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                 ) {
                     Icon(Icons.Default.PersonRemove, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                     Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                     Text("Unfriend")
                 }
                 Button(
                     onClick = { onAction(UserProfileAction.ChatWithFriend) },
                     modifier = Modifier.weight(1f)
                 ) {
                     Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                     Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                     Text("Message")
                 }
                 BlockButton(modifier = Modifier.weight(0.6f), onAction = onAction) // Smaller block button when friends
            }
            RelationshipStatus.BLOCKING -> {
                 // Current User is Blocking Profile User
                 Button(
                     onClick = { onAction(UserProfileAction.UnblockUser) },
                     modifier = Modifier.weight(1f),
                     colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
                 ) {
                     Icon(Icons.Default.CheckCircleOutline, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                     Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                     Text("Unblock")
                 }
            }
            RelationshipStatus.BLOCKED -> {
                 // Current User is Blocked BY Profile User
                 Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                     Text(
                         "You are blocked by this user", 
                         color = MaterialTheme.colorScheme.error,
                         style = MaterialTheme.typography.bodyMedium
                     )
                 }
            }
            null -> {
                 // No relationship info (e.g., error loading it)
                 // Optionally show Add Friend or an error indicator
                  Button(
                     onClick = { onAction(UserProfileAction.SendFriendRequest) },
                     modifier = Modifier.weight(1f)
                 ) {
                     Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                     Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                     Text("Add Friend")
                 }
                 BlockButton(modifier = Modifier.weight(1f), onAction = onAction)
            }
        }
    }
}

// Extracted Block Button for reuse
@Composable
private fun RowScope.BlockButton(modifier: Modifier = Modifier, onAction: (UserProfileAction) -> Unit) {
     OutlinedButton(
        onClick = { onAction(UserProfileAction.BlockUser) },
        modifier = modifier,
        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
    ) {
         Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
         Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text("Block")
    }
}

// Extracted Stats Row
@Composable
private fun RowScope.ProfileStatsRow(state: UserProfileState, onAction: (UserProfileAction) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatColumn(
            count = state.user?.followerCount ?: 0,
            label = "Followers",
            onClick = { state.user?.id?.let { onAction(UserProfileAction.NavigateFriendList(it)) } }
        )
        StatColumn(
            count = state.user?.followingCount ?: 0,
            label = "Following",
            onClick = { state.user?.id?.let { onAction(UserProfileAction.NavigateFriendList(it)) } }
        )
        StatColumn(
            count = state.posts.size,
            label = "Posts",
            onClick = { /* Maybe scroll to posts? */ }
        )
    }
}


@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun ProfileScreenPreview_Friend() {
    MainAppTheme {
         UserProfileScreen(
            state = UserProfileState(
                user = MockData.mockUsers.first().copy(bio = "This is a sample bio text.", followerCount = 123, followingCount = 45),
                posts = MockData.mockPosts.map { it.copy(user = MockData.mockUsers.first()) },
                relationshipInfo = RelationInfo(id="otherUser", status=RelationshipStatus.FRIENDS),
                currentUserId = "currentUser",
                isLoading = false,
                error = null
            ),
            onAction = {}
         )
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun ProfileScreenPreview_PendingIncoming() {
    MainAppTheme {
         UserProfileScreen(
            state = UserProfileState(
                user = MockData.mockUsers.first().copy(bio = "", followerCount = 10, followingCount = 5),
                posts = emptyList(),
                relationshipInfo = RelationInfo(id="otherUser", status=RelationshipStatus.PENDING_INCOMING),
                currentUserId = "currentUser",
                isLoading = false,
                error = null
            ),
            onAction = {}
         )
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun ProfileScreenPreview_None() {
    MainAppTheme {
         UserProfileScreen(
            state = UserProfileState(
                user = MockData.mockUsers.first().copy(bio = "Another user", followerCount = 0, followingCount = 0),
                posts = MockData.mockPosts.take(1).map { it.copy(user = MockData.mockUsers.first()) },
                relationshipInfo = null, // Or RelationInfo(status=RelationshipStatus.NONE)
                currentUserId = "currentUser",
                isLoading = false,
                error = null
            ),
            onAction = {}
         )
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun ProfileScreenPreview_Blocking() {
    MainAppTheme {
         UserProfileScreen(
            state = UserProfileState(
                user = MockData.mockUsers.first(),
                posts = emptyList(),
                relationshipInfo = RelationInfo(id="otherUser", status=RelationshipStatus.BLOCKING),
                 currentUserId = "currentUser",
                isLoading = false,
                error = null
            ),
            onAction = {}
         )
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun ProfileScreenPreview_OwnProfile() {
    MainAppTheme {
         UserProfileScreen(
            state = UserProfileState(
                user = MockData.mockUsers.first { it.id == "user1" }, // Example user ID
                posts = MockData.mockPosts.filter { it.userId == "user1" }.map { it.copy(user = MockData.mockUsers.first { u -> u.id == "user1" }) },
                relationshipInfo = null, // No relationship info for own profile
                currentUserId = "user1", // Current user ID matches profile user ID
                isLoading = false,
                error = null
            ),
            onAction = {}
         )
    }
}