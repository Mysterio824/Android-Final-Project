package com.androidfinalproject.hacktok.ui.mainDashboard.watchLater

import com.androidfinalproject.hacktok.model.enums.ReportCause
import com.androidfinalproject.hacktok.model.enums.ReportType

sealed class WatchLaterAction {
    data class RemovePost(val postId: String) : WatchLaterAction()
    data class OnPostClick(val postId: String) : WatchLaterAction()
    data class OnUserClick(val userId: String) : WatchLaterAction()
    data class OnCommentClick(val postId: String) : WatchLaterAction()
    data class OnLikeClick(val postId: String) : WatchLaterAction()
    data class OnUnLikeClick(val postId: String) : WatchLaterAction()
    data class SubmitReport(
        val reportedItemId: String,
        val reportType: ReportType,
        val reportCause: ReportCause
    ) : WatchLaterAction()
}