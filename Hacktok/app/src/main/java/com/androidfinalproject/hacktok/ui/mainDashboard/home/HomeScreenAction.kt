package com.androidfinalproject.hacktok.ui.mainDashboard.home

import com.androidfinalproject.hacktok.model.enums.ReportCause
import com.androidfinalproject.hacktok.model.enums.ReportType

sealed class HomeScreenAction {
    data class LikePost(val postId: String) : HomeScreenAction()
    data class SharePost(val postId: String) : HomeScreenAction()
    data object OnCreatePost: HomeScreenAction()
    data object OnCreateStory: HomeScreenAction()
    data class OnStoryClick(val storyId: String): HomeScreenAction()
    data class OnPostClick(val postId: String) : HomeScreenAction()
    data class OnUserClick(val userId: String) : HomeScreenAction()
    data class SubmitReport(
        val reportedItemId: String,
        val reportType: ReportType,
        val reportCause: ReportCause
    ) : HomeScreenAction()
}