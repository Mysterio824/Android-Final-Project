package com.androidfinalproject.hacktok.ui.mainDashboard;

import android.content.Context
import androidx.activity.result.ActivityResultLauncher

sealed class DashboardAction {
    data class SelectTab(val index: String) : DashboardAction()

    data class OnPostClick(val postId: String, val commentId: String?) : DashboardAction()
    data class OnUserClick(val userId: String) : DashboardAction()
    data class GotoUserChat(val userId: String) : DashboardAction()
    data class GotoGroupChat(val groupId: String) : DashboardAction()
    data class OnFriendListNavigate(val userId: String) : DashboardAction()
    data class OnPostEditNavigate(val postId: String) : DashboardAction()
    data class OnStoryClick(val storyId: String) : DashboardAction()
    object OnNavigateBack : DashboardAction()
    object OnMessageDashboardNavigate : DashboardAction()
    object OnCurrentProfileNavigate : DashboardAction()
    object OnSearchNavigate : DashboardAction()
    object OnCreatePost : DashboardAction()
    object OnCreateStory : DashboardAction()
    object OnUserEdit : DashboardAction()
    object OnChangePass : DashboardAction()
    object OnSecretCrushNavigate : DashboardAction()
    object OnAuthNavigate : DashboardAction()
    data class CheckNotificationPermission(
        val context: Context,
        val permissionLauncher: ActivityResultLauncher<String>
    ) : DashboardAction()
}