package com.androidfinalproject.hacktok.ui.mainDashboard.watchLater

sealed class WatchLaterAction {
    data class RemovePost(val postId: String) : WatchLaterAction()
    data class OnPostClick(val postId: String) : WatchLaterAction()
    data class OnUserClick(val userId: String) : WatchLaterAction()
    data class OnCommentClick(val postId: String) : WatchLaterAction()
    data class OnLikeClick(val postId: String) : WatchLaterAction()
}