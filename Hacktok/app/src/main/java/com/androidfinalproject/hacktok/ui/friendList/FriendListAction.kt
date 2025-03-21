package com.androidfinalproject.hacktok.ui.friendList

import org.bson.types.ObjectId

sealed class FriendListAction {
    data class SearchQueryChanged(val query: String) : FriendListAction()
    data class AddFriend(val userId: ObjectId?) : FriendListAction()
    data class ChatWithFriend(val userId: ObjectId?) : FriendListAction()
    data class UserClicked(val userId: ObjectId?) : FriendListAction()
    data object LoadFriends : FriendListAction()
    data object NavigateBack : FriendListAction()
}