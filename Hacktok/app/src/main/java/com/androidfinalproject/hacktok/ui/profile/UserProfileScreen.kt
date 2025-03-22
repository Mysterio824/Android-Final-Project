package com.androidfinalproject.hacktok.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.R
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.ui.profile.component.PostCard
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import com.androidfinalproject.hacktok.ui.post.component.PostContent

@Composable
fun UserProfileScreen (
    user: User,
    posts: List<Post>,
    isFriend: Boolean,
    isBlocked: Boolean,
    onSendFriendRequest: () -> Unit,
    onUnfriend: () -> Unit,
    onChat: () -> Unit,
    onBlock: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        // Profile Picture
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            androidx.compose.material3.Text(
                text = user.username.first().toString(),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // User Info
        Column (
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = user.username, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(text = user.email, fontSize = 16.sp, color = Color.Gray)
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("12") // Bold number
                    }
                    append(" Friends \u00B7 ") // Normal text
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("34") // Bold number
                    }
                    append(" Posts") // Normal text
                },
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Conditional Buttons
        if (isBlocked) {
            Button(
                onClick = onBlock,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF50C878)) // Red
            ) {
                Text("Unblock", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("You can not see this user's content.", color = Color.Red, fontSize = 16.sp)
        } else {
            Row {
                if (isFriend) {
                    Button(onClick = onUnfriend) {
                        Text("Unfriend", color= Color.White)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(onClick = onChat) {
                        Text("Chat")
                    }
                } else {
                    Button(onClick = onSendFriendRequest) {
                        Text("Add Friend")
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(onClick = onBlock, colors = ButtonDefaults.buttonColors(Color.Red)) {
                    Text("Block")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn (
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(posts) { post ->
                    PostContent(post = post, onLikeClick = { /* Handle like click */ }, onCommentClick = { /* Handle comment click */ }, onShareClick = {}, onUserClick = {})
                }
            }
        }
    }
}