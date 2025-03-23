package com.androidfinalproject.hacktok.ui.currentProfile

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.currentProfile.component.ActionButton
import com.androidfinalproject.hacktok.ui.currentProfile.component.StatColumn
import com.androidfinalproject.hacktok.ui.post.component.PostContent
import java.util.Locale

@Composable
fun CurrentProfileScreen(
    navController: NavController,
    user: User,
    posts: List<Post>,
    friendCount: Int,
    onGoToEditing: () -> Unit,
    onPostClick: (Post) -> Unit,
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val groupedPosts = posts.sortedByDescending { it.createdAt }
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
                text = user.username.first().toString(),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Text(text = user.username, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = user.email, fontSize = 16.sp, color = Color.Gray)

        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            StatColumn(friendCount, "Friends")
            StatColumn(posts.size, "Posts")
        }

        ActionButton(label = "Edit Profile", onClick = onGoToEditing)

//        horizontal line
        Divider(
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

                postsForDate.forEach { post ->
                    PostContent(
                        post = post,
                        onLikeClick = {},
                        onShareClick = {},
                        onUserClick = {},
                        onCommentClick = {})
                }
            }
        }
    }

}