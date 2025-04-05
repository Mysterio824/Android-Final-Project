package com.androidfinalproject.hacktok.ui.groupChat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.model.Group
import com.androidfinalproject.hacktok.model.User
import java.util.Date

@Composable
fun ManageGroupScreen(
    group: Group,
    membersList: List<User>,
    currentUserId: String,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    onRenameGroup: (String) -> Unit,
    onFindInChat: () -> Unit,
    onLeaveGroup: () -> Unit
) {
    var isRenameDialogVisible by remember { mutableStateOf(false) }
    var newGroupName by remember { mutableStateOf(group.groupName) }
    val isAdmin = group.admins.contains(currentUserId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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
                TextButton(onClick = { isRenameDialogVisible = true }) {
                    Text("Đổi tên nhóm", color = MaterialTheme.colorScheme.primary)
                }
            }
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

            IconButton(onClick = { /* No action yet */ }) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = "Group",
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

        Spacer(modifier = Modifier.height(16.dp))

        // Members list
        Text(
            text = "Thành viên",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(membersList) { member ->
                MemberItem(
                    user = member,
                    isAdmin = group.admins.contains(member.id),
                    isCreator = member.id == group.creatorId
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLeaveGroup,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
        ) {
            Text("Leave Group", color = Color.White)
        }
    }

    // Rename dialog
    if (isRenameDialogVisible) {
        AlertDialog(
            onDismissRequest = { isRenameDialogVisible = false },
            title = { Text("Đổi tên nhóm") },
            text = {
                TextField(
                    value = newGroupName,
                    onValueChange = { newGroupName = it },
                    label = { Text("Tên nhóm mới") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRenameGroup(newGroupName)
                        isRenameDialogVisible = false
                    }
                ) {
                    Text("Lưu")
                }
            },
            dismissButton = {
                Button(
                    onClick = { isRenameDialogVisible = false }
                ) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
fun MemberItem(user: User, isAdmin: Boolean, isCreator: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // User avatar
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.username.first().toString(),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = user.username,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isCreator) {
                    Text(
                        text = "Người tạo",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (isAdmin) {
                    Text(
                        text = "Admin",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        // Three dots menu button
        IconButton(onClick = { /* No action yet */ }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Menu",
                tint = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ManageGroupScreenPreview() {
    val group = Group(
        id = "group1",
        groupName = "Nhóm Dự Án Android",
        description = "Nhóm phát triển ứng dụng Android",
        creatorId = "user1",
        members = listOf("user1", "user2", "user3"),
        admins = listOf("user1"),
        isPublic = true,
        createdAt = Date()
    )

    val membersList = listOf(
        User(id = "user1", username = "User1", email = "user1@example.com"),
        User(id = "user2", username = "User2", email = "user2@example.com"),
        User(id = "user3", username = "User3", email = "user3@example.com")
    )

    ManageGroupScreen(
        group = group,
        membersList = membersList,
        currentUserId = "user1",
        isMuted = false,
        onToggleMute = {},
        onRenameGroup = {},
        onFindInChat = {},
        onLeaveGroup = {}
    )
}