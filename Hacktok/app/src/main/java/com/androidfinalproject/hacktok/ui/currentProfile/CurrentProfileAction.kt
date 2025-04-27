package com.androidfinalproject.hacktok.ui.currentProfile

import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.ui.newPost.PRIVACY

sealed class CurrentProfileAction {
    data class OnPostClick(val post: Post) : CurrentProfileAction()
    data class NavigateFriendList(val userId: String) : CurrentProfileAction()
    data class NavigateToPostEdit(val postId: String) : CurrentProfileAction()
    data object NavigateToProfileEdit : CurrentProfileAction()
    data object NavigateToNewPost : CurrentProfileAction()
    data class OnImageClick (val imageUrl: String) : CurrentProfileAction()
    object OnNavigateBack : CurrentProfileAction()
    object Refresh: CurrentProfileAction()
    data class OnLikesShowClick(val targetId: String): CurrentProfileAction()
    object RetryLoading : CurrentProfileAction()
    data class OnDeletePost(val postId: String) : CurrentProfileAction()
    data class OnUserClick(val userId: String) : CurrentProfileAction()
    data class UpdatePrivacy(val privacy: PRIVACY) : CurrentProfileAction()
    data class UpdateSharePost(val post: Post) : CurrentProfileAction()
    data class UpdateShareCaption(val caption: String) : CurrentProfileAction()
    data object ShowShareDialog : CurrentProfileAction()
    data object DismissShareDialog : CurrentProfileAction()
    data class OnSharePost(val post: Post, val caption: String, val privacy: PRIVACY) : CurrentProfileAction()
    data class OnLike(val postId: String, val emoji: String, val isLike: Boolean) : CurrentProfileAction()
}