package com.androidfinalproject.hacktok.ui.mainDashboard.watchLater

import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.enums.ReportCause
import com.androidfinalproject.hacktok.model.enums.ReportType
import com.androidfinalproject.hacktok.ui.newPost.PRIVACY

sealed class WatchLaterAction {
    data class RemovePost(val postId: String) : WatchLaterAction()
    data class OnPostClick(val postId: String) : WatchLaterAction()
    data class OnUserClick(val userId: String) : WatchLaterAction()
    data class OnLikeClick(val postId: String, val emoji: String) : WatchLaterAction()
    data class OnUnLikeClick(val postId: String) : WatchLaterAction()
    data class OnLikesShowClick(val targetId: String): WatchLaterAction()
    data class OnImageClick(val imageUrl: String) : WatchLaterAction()
    data class UpdateSharePost(val post: Post) : WatchLaterAction()
    data class OnSharePost(val post: Post, val caption: String, val privacy: PRIVACY) : WatchLaterAction()
    data class SharePost(val postId: String) : WatchLaterAction()
    data class UpdateSharePrivacy(val privacy: PRIVACY) : WatchLaterAction()
    data class UpdateShareCaption(val caption: String) : WatchLaterAction()
    data class DeletePost(val postId: String) : WatchLaterAction()
    data object DismissShareDialog : WatchLaterAction()
    data class SubmitReport(
        val reportedItemId: String,
        val reportType: ReportType,
        val reportCause: ReportCause
    ) : WatchLaterAction()
}