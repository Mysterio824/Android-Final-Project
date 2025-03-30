package com.androidfinalproject.hacktok.ui.mainDashboard;

sealed class DashboardAction {
    object LoadPosts : DashboardAction()
    data class LikePost(val postId: String) : DashboardAction()
    data class SharePost(val postId: String) : DashboardAction()
}