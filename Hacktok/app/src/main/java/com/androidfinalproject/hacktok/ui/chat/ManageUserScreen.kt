package com.androidfinalproject.hacktok.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.androidfinalproject.hacktok.model.User

@Composable
fun ManageUserScreen(
    user: User,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    onCreateGroup: () -> Unit,
    onFindInChat: () -> Unit,
    onDeleteChat: () -> Unit,
    onBlockUser: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ảnh đại diện
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.username.first().toString(),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = user.username, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(text = user.email, fontSize = 16.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggleMute) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.NotificationsOff else Icons.Default.Notifications,
                    contentDescription = "Toggle Mute",
                    tint = if (isMuted) Color.Red else Color.Gray
                )
            }

            IconButton(onClick = onCreateGroup) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = "Create Group",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = onFindInChat) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Find in Chat",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(400.dp))

        Button(
            onClick = onDeleteChat,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
        ) {
            Text("Delete Chat", color = Color.White)
        }

        Spacer(modifier = Modifier.height(1.dp))

        Button(
            onClick = onBlockUser,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
        ) {
            Text("Blok this user", color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewManageUserScreen() {
    ManageUserScreen(
        user = User(username = "User123", email = "user123@example.com"),
        isMuted = false,
        onToggleMute = {},
        onCreateGroup = {},
        onFindInChat = {},
        onDeleteChat = {},
        onBlockUser = {}
    )
}
