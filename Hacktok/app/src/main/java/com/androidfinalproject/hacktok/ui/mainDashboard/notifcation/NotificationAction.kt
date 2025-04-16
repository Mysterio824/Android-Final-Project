package com.androidfinalproject.hacktok.ui.mainDashboard.notifcation

sealed class NotificationAction {
    data class OnUserClick(val userId: String) : NotificationAction()
    data class OnPostClick(val postId: String) : NotificationAction()
    object OnNavigationBack : NotificationAction()
    data class OnMarkAsRead(val notificationId: String) : NotificationAction()
    data class OnDeleteNotification(val notificationId: String) : NotificationAction()
    object OnRefresh : NotificationAction()
}