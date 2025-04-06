package com.androidfinalproject.hacktok.ui.mainDashboard;

sealed class DashboardAction {
    data class SelectTab(val index: String) : DashboardAction()
    data class OnPostClick(val postId: String) : DashboardAction()
    data class OnUserClick(val userId: String) : DashboardAction()
    data class GotoUserChat(val userId: String) : DashboardAction()
    data class GotoGroupChat(val groupId: String) : DashboardAction()
    data class OnFriendListNavigate(val userId: String) : DashboardAction()
    data object OnEditProfileNavigate : DashboardAction()
    data class OnPostEditNavigate(val postId: String) : DashboardAction()
}