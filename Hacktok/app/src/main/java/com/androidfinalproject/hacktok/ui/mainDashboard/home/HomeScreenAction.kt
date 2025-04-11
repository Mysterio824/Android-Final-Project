package com.androidfinalproject.hacktok.ui.mainDashboard.home

sealed class HomeScreenAction {
    object UploadPost: HomeScreenAction()
    data class UpdateStatusText(val text: String): HomeScreenAction()
    data class LikePost(val postId: String) : HomeScreenAction()
    data class SharePost(val postId: String) : HomeScreenAction()
    data class OnUserClick(val userId: String) : HomeScreenAction()
    data class OnPostClick(val postId: String) : HomeScreenAction()
    data class SelectTab(val tabIndex: Int) : HomeScreenAction()
    object OnCreatePost : HomeScreenAction()
    object OnCreateStory : HomeScreenAction()
    data class OnStoryClick(val storyId: String) : HomeScreenAction()
}