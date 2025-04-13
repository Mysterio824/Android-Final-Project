package com.androidfinalproject.hacktok.ui.chatDetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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
fun MembersList(
    membersList: List<User>,
    group: Group,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(membersList) { member ->
            MemberItem(
                user = member,
                isAdmin = group.admins.contains(member.id),
                isCreator = member.id == group.creatorId
            )
        }
    }
}

@Composable
fun MemberItem(
    user: User,
    isAdmin: Boolean,
    isCreator: Boolean
) {
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
fun MembersListPreview() {
    val previewGroup = Group(
        id = "group1",
        groupName = "Nhóm Preview",
        description = "Mô tả cho nhóm",
        creatorId = "user1",
        members = listOf("user1", "user2", "user3"),
        admins = listOf("user1", "user2"),
        isPublic = true,
        createdAt = Date(),
        coverImage = null
    )

    val membersList = listOf(
        User(id = "user1", username = "Creator User", email = "creator@example.com"),
        User(id = "user2", username = "Admin User", email = "admin@example.com"),
        User(id = "user3", username = "Normal User", email = "normal@example.com")
    )

    MaterialTheme {
        MembersList(
            membersList = membersList,
            group = previewGroup,
            modifier = Modifier.height(300.dp)
        )
    }
}