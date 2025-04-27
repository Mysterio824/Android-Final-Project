package com.androidfinalproject.hacktok.ui.profile

import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.enums.ReportCause
import com.androidfinalproject.hacktok.model.enums.ReportType
import com.androidfinalproject.hacktok.ui.newPost.PRIVACY

sealed class UserProfileAction {
    // Actions initiated by the current user towards the profile user
    data object SendFriendRequest : UserProfileAction()
    data object CancelFriendRequest : UserProfileAction()
    data object Unfriend : UserProfileAction()
    data object BlockUser : UserProfileAction()
    data object UnblockUser : UserProfileAction()

    // Actions responding to the profile user's request
    data object AcceptFriendRequest : UserProfileAction()
    data object DeclineFriendRequest : UserProfileAction()
    
    // Navigation/UI Actions
    data object ChatWithFriend : UserProfileAction()
    data class GoToPost(val postId : String) : UserProfileAction()
    data class OnUserClick (val userId: String) : UserProfileAction()
    data object NavigateFriendList : UserProfileAction()
    data object NavigateBack : UserProfileAction()
    data class OnImageClick(val imageUrl: String) : UserProfileAction()

    // Data Actions
    data class LikePost(val postId: String, val emoji: String) : UserProfileAction()
    data class UnlikePost(val postId: String) : UserProfileAction()
    data class OnSavePost(val postId: String) : UserProfileAction()
    data object RefreshProfile : UserProfileAction()
    data object MessageUser : UserProfileAction()
    data class OnLikesShowClick(val targetId: String): UserProfileAction()
    data class SubmitReport(
        val reportedItemId: String,
        val reportType: ReportType,
        val reportCause: ReportCause
    ) : UserProfileAction()

    // Share actions
    data object ShowShareDialog : UserProfileAction()
    data object DismissShareDialog : UserProfileAction()
    data class UpdateSharePost(val post: Post) : UserProfileAction()
    data class UpdateSharePrivacy(val privacy: PRIVACY) : UserProfileAction()
    data class UpdateShareCaption(val caption: String) : UserProfileAction()
    data class OnSharePost(val post: Post, val caption: String, val privacy: PRIVACY) : UserProfileAction()
}