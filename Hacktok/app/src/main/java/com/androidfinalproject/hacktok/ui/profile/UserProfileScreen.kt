package com.androidfinalproject.hacktok.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
import com.androidfinalproject.hacktok.ui.mainDashboard.currentProfile.component.StatColumn
import com.androidfinalproject.hacktok.ui.post.component.PostContent
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme
import com.androidfinalproject.hacktok.ui.profile.component.IconTextButton

@Composable
fun UserProfileScreen (
    state : UserProfileState,
    onAction : (UserProfileAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        // Profile Picture
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = state.user!!.username.first().toString(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Column {
                Text(text = state.user?.fullName ?: "Full Name", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(text = "@" + state.user?.username, fontSize = 16.sp, color = Color.Gray)
            }
        }

        Text(state.user?.bio ?: "Sunny days wouldn't be special if it wasn't for rain. Joy wouldn't feel so good if it wasn't for pain.", fontSize = 15.sp, color = Color.DarkGray, modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp))

        if (state.isBlocked) {
            IconTextButton(
                icon = Icons.Default.LockOpen,
                text = "Unblock",
                onClick = {  },
                backgroundColor = MaterialTheme.colorScheme.primary
            )
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (state.isFriend) {
                    IconTextButton(
                        icon = Icons.Default.PersonRemove,
                        text = "Unfriend",
                        onClick = { onAction(UserProfileAction.Unfriend) },
                        backgroundColor = Color.Gray
                    )

                    IconTextButton(
                        icon = Icons.Default.Chat,
                        text = "Chat",
                        onClick = { onAction(UserProfileAction.ChatWithFriend) },
                        backgroundColor = MaterialTheme.colorScheme.primary
                    )
                } else {
                    IconTextButton(
                        icon = Icons.Default.PersonAdd,
                        text = "Add Friend",
                        onClick = { onAction(UserProfileAction.AddFriend) },
                        backgroundColor = MaterialTheme.colorScheme.primary
                    )
                }

                IconTextButton(
                    icon = Icons.Default.Block,
                    text = "Block",
                    onClick = { onAction(UserProfileAction.BlockUser) },
                    backgroundColor = Color.Red
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(
            color = Color.LightGray,
            thickness = 1.dp,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            StatColumn(
                state.user?.friends?.size ?: 0,
                "Friends",
                onClick = {}
            )
            StatColumn(
                state.posts.size,
                "Posts",
                onClick = {}
            )
        }

        HorizontalDivider(
            color = Color.LightGray,
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )

        if (!state.isBlocked) {
            LazyColumn (
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.posts) { post ->
                    PostContent(
                        post = post,
                        onPostClick = {
                            onAction(UserProfileAction.GoToPost(post.id ?: ""))
                        },
                        onToggleLike = {
                            onAction(UserProfileAction.LikePost(post.id ?: ""))
                        },
                        onUserClick = {
                            onAction(UserProfileAction.RefreshProfile)
                        },
                        onComment = {
                            onAction(UserProfileAction.GoToPost(post.id ?: ""))
                        },
                        onShare = {},
                        onOptionsClick = {},
                    )
                }
            }
        } else {
            Text("User has been blocked.", color = Color.Red, fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ){
            UserProfileScreen(
                state = UserProfileState(
                    user = MockData.mockUsers.first(),
                    posts = MockData.mockPosts,
                    isBlocked = false,
                    isFriend = false
                ),
                onAction = {}
            )
        }
    }
}