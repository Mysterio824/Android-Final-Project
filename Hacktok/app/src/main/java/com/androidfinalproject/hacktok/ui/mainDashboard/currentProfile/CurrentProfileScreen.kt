package com.androidfinalproject.hacktok.ui.mainDashboard.currentProfile

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
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
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
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

        Text(text = state.user!!.username, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = state.user!!.email, fontSize = 16.sp, color = Color.Gray)

        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            StatColumn(state.friendCount, "Friends")
            StatColumn(state.posts.size, "Posts")
        }

        ActionButton(label = "Edit Profile", onClick = { onAction(CurrentProfileAction.NavigateToProfileEdit) })

        HorizontalDivider(
            color = Color.LightGray,
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Text(
            text = "Manage posts",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 16.dp)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            groupedPosts.forEach { (date, postsForDate) ->
                Text(
                    text = date,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
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