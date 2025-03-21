package com.androidfinalproject.hacktok.ui.profile
import org.bson.types.ObjectId

sealed class UserProfileAction {
    data class AddFriend(val userId: ObjectId?) : UserProfileAction()
    data class Unfriend(val userId: ObjectId?) : UserProfileAction()
    data class ChatWithFriend(val userId: ObjectId?) : UserProfileAction()
    data class GoToPost(val postId: ObjectId?) : UserProfileAction()
    data class BlockUser(val userId: ObjectId?) : UserProfileAction()
    data object RefreshProfile : UserProfileAction()
    data object NavigateBack : UserProfileAction()
}