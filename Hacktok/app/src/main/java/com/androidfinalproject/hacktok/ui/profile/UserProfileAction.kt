package com.androidfinalproject.hacktok.ui.profile

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
    data object NavigateFriendList : UserProfileAction()
    data object NavigateBack : UserProfileAction()
    
    // Data Actions
    data class LikePost(val postId: String) : UserProfileAction()
    data object RefreshProfile : UserProfileAction()
    data object MessageUser : UserProfileAction()
}