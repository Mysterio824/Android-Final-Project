package com.androidfinalproject.hacktok.ui.mainDashboard.notifcation

import com.androidfinalproject.hacktok.model.Notification

data class NotificationState(
    val notifications: List<Notification> = emptyList(),
    val navigateComment: Boolean = false,
    val postId: String = "",
    val commentId: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)