package com.androidfinalproject.hacktok.ui.chatDetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.model.Group
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.chatDetail.ChatDetailAction

@Composable
fun MembersList(
    membersList: List<User>,
    group: Group,
    modifier: Modifier = Modifier,
    onMemberAction: ((ChatDetailAction) -> Unit)? = null
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(membersList) { member ->
            MemberItem(
                user = member,
                isAdmin = group.admins.contains(member.id),
                isCreator = member.id == group.creatorId,
                onMemberAction = onMemberAction
            )

            Divider(
                color = Color(0xFFECECEC),
                thickness = 1.dp,
                modifier = Modifier.padding(start = 52.dp)
            )
        }
    }
}

@Composable
fun MemberItem(
    user: User,
    isAdmin: Boolean,
    isCreator: Boolean,
    onMemberAction: ((ChatDetailAction) -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // User avatar
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF72BF6A).copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.username.firstOrNull()?.toString() ?: "",
                color = Color(0xFF72BF6A),
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
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isCreator) {
                    Text(
                        text = "Người tạo",
                        fontSize = 12.sp,
                        color = Color(0xFF72BF6A)
                    )
                }

                if (isAdmin && !isCreator) {
                    Text(
                        text = "Admin",
                        fontSize = 12.sp,
                        color = Color(0xFF72BF6A).copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Three dots menu button
        IconButton(onClick = {
            onMemberAction?.invoke(ChatDetailAction.NavigateToUserProfile(user.id))
        }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Menu",
                tint = Color.Gray
            )
        }
    }
}