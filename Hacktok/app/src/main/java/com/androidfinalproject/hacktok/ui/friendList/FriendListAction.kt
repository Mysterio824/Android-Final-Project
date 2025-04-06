package com.androidfinalproject.hacktok.ui.friendList

import com.androidfinalproject.hacktok.model.User

sealed class FriendListAction {
    data class SearchQueryChanged(val query: String) : FriendListAction()
    data class AddFriend(val user: User) : FriendListAction()
    data class ChatWithFriend(val user: User) : FriendListAction()
    data class UserClicked(val user: User) : FriendListAction()
    data object NavigateBack : FriendListAction()
}