package com.androidfinalproject.hacktok.ui.profile

sealed class UserProfileAction {
    data object AddFriend : UserProfileAction()
    data object Unfriend : UserProfileAction()
    data object ChatWithFriend : UserProfileAction()
    data class GoToPost(val postId : String) : UserProfileAction()
    data object BlockUser : UserProfileAction()
    data object RefreshProfile : UserProfileAction()
    data object NavigateBack : UserProfileAction()
}