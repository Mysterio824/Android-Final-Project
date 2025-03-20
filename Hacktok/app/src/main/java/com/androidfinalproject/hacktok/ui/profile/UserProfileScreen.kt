package com.androidfinalproject.hacktok.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
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

@Composable
fun UserProfileScreen (
    user: User,
    posts: List<Post>,
    isFriend: Boolean,
    isBlocked: Boolean,
    onSendFriendRequest: () -> Unit,
    onUnfriend: () -> Unit,
    onChat: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        // Profile Picture
        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.profile_placeholder),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // User Info
        Column (
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = user.username, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(text = user.email, fontSize = 16.sp, color = Color.Gray)
            Text(text = "12 Friends \u00B7 34 Posts", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Conditional Buttons
        if (isBlocked) {
            Text(text = "You have blocked this user", color = Color.Red)
        } else {
            Row {
                if (isFriend) {
                    Button(onClick = onUnfriend, colors = ButtonDefaults.buttonColors(Color.Red)) {
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
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(posts) { post ->
                PostCard(post)
            }
        }
    }
}