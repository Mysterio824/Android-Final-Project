package com.androidfinalproject.hacktok.ui.profile

sealed class UserProfileAction {
    data class AddFriend(val userId: String?) : UserProfileAction()
    data class Unfriend(val userId: String?) : UserProfileAction()
    data class ChatWithFriend(val userId: String?) : UserProfileAction()
    data class GoToPost(val postId: String?) : UserProfileAction()
    data class BlockUser(val userId: String?) : UserProfileAction()
    data object RefreshProfile : UserProfileAction()
    data object NavigateBack : UserProfileAction()
}