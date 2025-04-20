package com.androidfinalproject.hacktok.ui.mainDashboard.notifcation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.tooling.preview.Preview
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.ui.mainDashboard.notifcation.component.*
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme
@Composable
fun NotificationScreen(
    state: NotificationState,
    onAction: (NotificationAction) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAction(NotificationAction.OnRefresh) },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading && state.notifications.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            } else if (state.error != null && state.notifications.isEmpty()) {
                ErrorView(message = state.error, onRetry = { onAction(NotificationAction.OnRefresh) })
            } else if (state.notifications.isEmpty()) {
                EmptyNotifications(modifier = Modifier.fillMaxSize())
            } else {
                NotificationList(
                    notifications = state.notifications,
                    onUserClick = { userId -> onAction(NotificationAction.OnUserClick(userId)) },
                    onPostClick = { postId -> onAction(NotificationAction.OnPostClick(postId)) },
                    onCommentClick = { commentId -> onAction(NotificationAction.OnCommentClick(commentId)) },
                    onMarkAsRead = { notificationId -> onAction(NotificationAction.OnMarkAsRead(notificationId)) },
                    onDelete = { notificationId -> onAction(NotificationAction.OnDeleteNotification(notificationId)) },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun ScreenPreview(){
    MainAppTheme {
        Box()
        {

            NotificationScreen(
                state = NotificationState(
                    notifications = MockData.getMockNotifications(10),
                    isLoading = false
                ),
                onAction = {})
        }
    }
}