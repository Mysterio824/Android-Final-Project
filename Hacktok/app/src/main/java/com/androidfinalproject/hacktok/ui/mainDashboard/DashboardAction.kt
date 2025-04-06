package com.androidfinalproject.hacktok.ui.mainDashboard;

sealed class DashboardAction {
    object LoadPosts : DashboardAction()
    object UploadPost: DashboardAction()
    data class UpdateStatusText(val text: String): DashboardAction()
    data class LikePost(val postId: String) : DashboardAction()
    data class SharePost(val postId: String) : DashboardAction()
    data class UserClick(val userId: String) : DashboardAction()
    data class PostClick(val postId: String) : DashboardAction()
}