package com.androidfinalproject.hacktok.ui.mainDashboard.notifcation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Announcement
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.Notification
import com.androidfinalproject.hacktok.model.enums.NotificationType
import com.androidfinalproject.hacktok.ui.commonComponent.ProfileImage
import com.androidfinalproject.hacktok.ui.mainDashboard.notifcation.NotificationAction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun NotificationItem(
    notification: Notification,
    onUserClick: (String) -> Unit,
    onPostClick: (String) -> Unit,
    onCommentClick: (String) -> Unit,
    onMarkAsRead: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    val backgroundColor = if (!notification.isRead) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        color = backgroundColor,
        tonalElevation = if (!notification.isRead) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    notification.id?.let { onMarkAsRead(it) }
                    when (notification.type) {
                        NotificationType.FRIEND_REQUEST -> notification.senderId?.let { onUserClick(it) }
                        NotificationType.FRIEND_ACCEPT -> notification.senderId?.let { onUserClick(it) }
                        NotificationType.POST_LIKE -> notification.relatedId?.let { onPostClick(it) }
                        NotificationType.POST_COMMENT -> notification.relatedId?.let { onPostClick(it) }
                        NotificationType.COMMENT_REPLY -> notification.relatedId?.let { onCommentClick(it) }
                        NotificationType.COMMENT_LIKE -> notification.relatedId?.let { onCommentClick(it) }
                        NotificationType.ADMIN_NOTIFICATION -> {
                            notification.actionUrl?.let { _ ->
                                // Handle action URL
                            }
                        }

                        NotificationType.NEW_STORY -> TODO()
                        NotificationType.NEW_MESSAGE -> TODO()
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image with notification type indicator
            Box(contentAlignment = Alignment.BottomEnd) {
                notification.senderImage?.let { imageUrl ->
                    ProfileImage(
                        imageUrl = imageUrl,
                        onClick = { notification.senderId?.let { onUserClick(it) } },
                        size = 50.dp
                    )
                }

                // Icon badge based on notification type
                Surface(
                    shape = CircleShape,
                    color = getNotificationTypeColor(notification.type),
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = getNotificationTypeIcon(notification.type),
                        contentDescription = notification.type.name,
                        tint = Color.White,
                        modifier = Modifier
                            .padding(4.dp)
                            .size(16.dp)
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    buildAnnotatedString {
                        notification.senderName?.let {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(it)
                                append(" ")
                            }
                        }
                        append(notification.content)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Time
                val formattedTime = formatTimeAgo(notification.createdAt)
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Priority indicator for high priority notifications
            if (notification.priority == "high") {
                Badge(
                    containerColor = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(8.dp)
                )
            }

            IconButton(
                onClick = { notification.id?.let { onDelete(it) } },
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete notification",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun getNotificationTypeIcon(type: NotificationType): ImageVector {
    return when (type) {
        NotificationType.FRIEND_REQUEST -> Icons.Default.PersonAdd
        NotificationType.FRIEND_ACCEPT -> Icons.Default.People
        NotificationType.POST_LIKE -> Icons.Default.Favorite
        NotificationType.POST_COMMENT -> Icons.Default.ChatBubble
        NotificationType.COMMENT_REPLY -> Icons.AutoMirrored.Filled.Reply
        NotificationType.COMMENT_LIKE -> Icons.Default.ThumbUp
        NotificationType.ADMIN_NOTIFICATION -> Icons.AutoMirrored.Filled.Announcement
        NotificationType.NEW_STORY -> Icons.Default.ViewCarousel
        NotificationType.NEW_MESSAGE -> TODO()
    }
}

private fun getNotificationTypeColor(type: NotificationType): Color {
    return when (type) {
        NotificationType.FRIEND_REQUEST,
        NotificationType.FRIEND_ACCEPT -> Color(0xFF1877F2) // Facebook blue
        NotificationType.POST_LIKE,
        NotificationType.COMMENT_LIKE -> Color(0xFFE41E3F) // Red for likes
        NotificationType.POST_COMMENT,
        NotificationType.COMMENT_REPLY -> Color(0xFF0BC5EA) // Light blue for comments
        NotificationType.ADMIN_NOTIFICATION -> Color(0xFFFF8800) // Orange for admin notifications
        NotificationType.NEW_STORY -> TODO()
        NotificationType.NEW_MESSAGE -> TODO()
    }
}

private fun formatTimeAgo(date: Date): String {
    val now = Date()
    val seconds = (now.time - date.time) / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "Just now"
        minutes < 60 -> "$minutes m"
        hours < 24 -> "$hours h"
        days < 7 -> "$days d"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
    }
}
