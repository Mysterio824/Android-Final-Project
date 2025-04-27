package com.androidfinalproject.hacktok.ui.mainDashboard.home

import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.enums.ReportCause
import com.androidfinalproject.hacktok.model.enums.ReportType
import com.androidfinalproject.hacktok.ui.newPost.PRIVACY

sealed class HomeScreenAction {
    data class LikePost(val postId: String) : HomeScreenAction()
    data class UnLikePost(val postId: String) : HomeScreenAction()
    data class SharePost(val postId: String) : HomeScreenAction()
    data object OnCreatePost: HomeScreenAction()
    data object OnCreateStory: HomeScreenAction()
    data class OnStoryClick(val storyId: String): HomeScreenAction()
    data class OnPostClick(val postId: String) : HomeScreenAction()
    data class OnUserClick(val userId: String) : HomeScreenAction()
    data class OnLikesShowClick(val targetId: String): HomeScreenAction()
    data class SubmitReport(
        val reportedItemId: String,
        val reportType: ReportType,
        val reportCause: ReportCause
    ) : HomeScreenAction()
    data object ShowShareDialog : HomeScreenAction()
    data class UpdateSharePost(val post: Post) : HomeScreenAction()
    data class UpdateSharePrivacy(val privacy: PRIVACY) : HomeScreenAction()
    data class UpdateShareCaption(val caption: String) : HomeScreenAction()
    data class OnSharePost(val post: Post, val caption: String, val privacy: PRIVACY) : HomeScreenAction()
    data object DismissShareDialog : HomeScreenAction()
    data object LoadMorePosts : HomeScreenAction()
    data object Refresh : HomeScreenAction()
    data object LoadMoreStories : HomeScreenAction()
    data object OnAdClick : HomeScreenAction()
    data object OnAdInterested : HomeScreenAction()
    data object OnAdUninterested : HomeScreenAction()
}