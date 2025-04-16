package com.androidfinalproject.hacktok.ui.friendList.component

import androidx.browser.customtabs.CustomTabsService.Relation
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.model.RelationInfo
import com.androidfinalproject.hacktok.model.enums.RelationshipStatus
import com.androidfinalproject.hacktok.model.User

@Composable
fun FriendListItem(
    user: User,
    relation: RelationInfo,
    onSendFriendRequest: (send: Boolean) -> Unit,
    onAcceptRequest: (accept: Boolean) -> Unit,
    onOptionsClick: () -> Unit,
    onUserClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 72.dp) // Ensures consistent height
            .padding(horizontal = 16.dp)
            .clickable(onClick = onUserClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // User avatar
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.username?.firstOrNull()?.toString()?.uppercase() ?: "?",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = user.username ?: "Unknown User",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = user.email,
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Action Buttons
        Row {
            when (relation.status) {
                RelationshipStatus.NONE -> {
                    Button(
                        onClick = { onSendFriendRequest(true) },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Friend",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Friend")
                    }
                }

                RelationshipStatus.FRIENDS -> {
                    IconButton(onClick = onOptionsClick) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options"
                        )
                    }
                }

                RelationshipStatus.PENDING_INCOMING -> {
                    Row {
                        Button(
                            onClick = { onAcceptRequest(true) },
                            contentPadding = PaddingValues(12.dp, 6.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) { Text("Accept") }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = { onAcceptRequest(false) },
                            contentPadding = PaddingValues(12.dp, 6.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) { Text("Decline") }
                    }
                }

                RelationshipStatus.PENDING_OUTGOING -> {
                    Button(
                        onClick = { onSendFriendRequest(false) },
                        contentPadding = PaddingValues(12.dp, 6.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Cancel")
                    }
                }

                else -> {}
            }
        }
    }

    HorizontalDivider(
        color = Color.LightGray,
        thickness = 1.dp,
        modifier = Modifier.padding(top = 8.dp)
    )
}