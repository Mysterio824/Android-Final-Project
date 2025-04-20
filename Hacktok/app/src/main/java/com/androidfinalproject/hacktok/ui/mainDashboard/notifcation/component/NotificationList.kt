package com.androidfinalproject.hacktok.ui.mainDashboard.notifcation.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.Notification

@Composable
fun NotificationList(
    notifications: List<Notification>,
    onUserClick: (String) -> Unit,
    onPostClick: (String) -> Unit,
    onCommentClick: (String) -> Unit,
    onMarkAsRead: (String) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(notifications) { notification ->
            NotificationItem(
                notification = notification,
                onUserClick = onUserClick,
                onPostClick = onPostClick,
                onCommentClick = onCommentClick,
                onMarkAsRead = onMarkAsRead,
                onDelete = onDelete,
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
        }
    }
}