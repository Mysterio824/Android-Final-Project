package com.androidfinalproject.hacktok.ui.mainDashboard.currentProfile

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.ui.mainDashboard.currentProfile.component.ActionButton
import com.androidfinalproject.hacktok.ui.mainDashboard.currentProfile.component.StatColumn
import com.androidfinalproject.hacktok.ui.post.component.EditPostContent
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme
import java.util.Locale

@Composable
fun CurrentProfileScreen(
    state : CurrentProfileState,
    onAction: (CurrentProfileAction) -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val groupedPosts = state.posts.sortedByDescending { it.createdAt }
        .groupBy { dateFormat.format(it.createdAt) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp, top = 24.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.size(80.dp)) {
                // Profile Picture (or Initial)
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(
                        text = state.user.username.first().uppercaseChar().toString(),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                IconButton(
                    onClick = { onAction(CurrentProfileAction.NavigateToProfileEdit) },
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.BottomEnd)
                        .background(Color(0xFF4C7EFF), CircleShape)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.White
                    )
                }
            }

            Column {
                Text(text = state.user.fullName ?: "John Terry", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(text = "@" + state.user.username, fontSize = 14.sp, color = Color.Gray)
            }
        }

        Text(state.user.bio ?: "Sunny days wouldn't be special if it wasn't for rain. Joy wouldn't feel so good if it wasn't for pain.", fontSize = 15.sp, color = Color.DarkGray, modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp))

        HorizontalDivider(
            color = Color.LightGray,
            thickness = 1.dp,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            StatColumn(
                state.friendCount,
                "Friends",
                onClick = {
                    onAction(CurrentProfileAction.NavigateFriendList)
                }
            )
            StatColumn(
                state.posts.size,
                "Posts",
                onClick = {}
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(
            color = Color.LightGray,
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Your posts",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth() // light gray background
                .clickable { onAction(CurrentProfileAction.NavigateToNewPost) }
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = state.user.username.first().uppercaseChar().toString(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Text("What's on your mind?")
        }

        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2E2E2E), // Dark gray background
                contentColor = Color.White         // White icon and text
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Forum, // You can use a better match if available
                contentDescription = "Manage posts",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Manage posts",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            groupedPosts.forEach { (date, postsForDate) ->
                Text(
                    text = date,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 16.dp, top = 24.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(state.posts) { post ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RectangleShape
                        ) {
                            EditPostContent(
                                post = post,
                                onLikeClick = {},
                                onShareClick = {},
                                onUserClick = {},
                                onCommentClick = {},
                                onEditClick = {
                                    onAction(
                                        CurrentProfileAction.NavigateToPostEdit(
                                            post
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CurrentProfileScreenPreview() {
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            CurrentProfileScreen(
                state = CurrentProfileState(
                    user = MockData.mockUsers.first(),
                    posts = MockData.mockPosts
                ),
                onAction = {}
            )
        }
    }
}