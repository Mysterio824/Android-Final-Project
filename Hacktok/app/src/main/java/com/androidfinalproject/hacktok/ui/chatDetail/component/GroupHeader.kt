package com.androidfinalproject.hacktok.ui.chatDetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFF72BF6A).copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = group.groupName.firstOrNull()?.toString() ?: "#",
                color = Color(0xFF72BF6A),
                fontSize = 36.sp,
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
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
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
                TextButton(
                    onClick = onRenameClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF72BF6A)
                    )
                ) {
                    Text(
                        "Đổi tên nhóm",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}