package com.androidfinalproject.hacktok.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.tooling.preview.Preview
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.ui.post.component.PostContent
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

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

        Spacer(modifier = Modifier.height(16.dp))

        // User Info
        Column (
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = state.user!!.username, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(text = state.user.email, fontSize = 16.sp, color = Color.Gray)
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
        if (state.isBlocked) {
            Button(
                onClick = { onAction(UserProfileAction.BlockUser) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Unblock", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("You can not see this user's content.", color = Color.Red, fontSize = 16.sp)
        } else {
            Row {
                if (state.isFriend) {
                    Button(onClick = { onAction(UserProfileAction.Unfriend) }) {
                        Text("Unfriend", color= Color.White)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(onClick = { onAction(UserProfileAction.ChatWithFriend) }) {
                        Text("Chat")
                    }
                } else {
                    Button(onClick = { onAction(UserProfileAction.AddFriend) }) {
                        Text("Add Friend")
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    onClick = { onAction(UserProfileAction.BlockUser) },
                    colors = ButtonDefaults.buttonColors(Color.Red)) {
                    Text("Block")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn (
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.posts) { post ->
                    PostContent(
                        post = post,
                        onLikeClick = { onAction(UserProfileAction.GoToPost(post.id!!)) },
                        onCommentClick = { onAction(UserProfileAction.GoToPost(post.id!!)) },
                        onShareClick = {},
                        onUserClick = {}
                    )
                }
            }
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
                    posts = MockData.mockPosts
                ),
                onAction = {}
            )
        }
    }
}