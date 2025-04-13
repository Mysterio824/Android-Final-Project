package com.androidfinalproject.hacktok.ui.chatDetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.model.Group
import com.androidfinalproject.hacktok.model.User
import java.util.Date

@Composable
fun GroupHeader(
    group: Group,
    currentUser: User,
    membersList: List<User>,
    onRenameClick: () -> Unit
) {
    val isAdmin = group.admins.contains(currentUser.id)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Group icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = group.groupName.first().toString(),
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
            Text(
                text = group.groupName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${group.members.size} thành viên",
                fontSize = 16.sp,
                color = Color.Gray
            )

            // Show creator info
            val creator = membersList.find { it.id == group.creatorId }
            if (creator != null) {
                Text(
                    text = "Người tạo: ${creator.username}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Show rename button if current user is admin
            if (isAdmin) {
                TextButton(onClick = onRenameClick) {
                    Text("Đổi tên nhóm", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupHeaderPreview() {
    val previewGroup = Group(
        id = "group1",
        groupName = "Nhóm Preview",
        description = "Mô tả cho nhóm",
        creatorId = "user1",
        members = listOf("user1", "user2", "user3"),
        admins = listOf("user1"),
        isPublic = true,
        createdAt = Date(),
        coverImage = null
    )

    val currentUser = User(
        id = "user1",
        username = "Preview User",
        email = "preview@example.com"
    )

    val membersList = listOf(
        User(id = "user1", username = "Preview User", email = "preview@example.com"),
        User(id = "user2", username = "Member 2", email = "member2@example.com"),
        User(id = "user3", username = "Member 3", email = "member3@example.com")
    )

    MaterialTheme {
        GroupHeader(
            group = previewGroup,
            currentUser = currentUser,
            membersList = membersList,
            onRenameClick = {}
        )
    }
}