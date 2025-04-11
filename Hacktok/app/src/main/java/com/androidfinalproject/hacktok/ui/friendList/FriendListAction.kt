package com.androidfinalproject.hacktok.ui.friendList

sealed class FriendListAction {
    data class SearchQueryChanged(val query: String) : FriendListAction()
    data class SendFriendRequest(val userId: String, val isSend: Boolean) : FriendListAction()
    data class OnAcceptFriendRequest(val userId: String, val isAccepted: Boolean) : FriendListAction()
    data class OnUnBlockFriend(val userId: String) : FriendListAction()
    data class OnBlockFriend(val userId: String) : FriendListAction()
    data class ChatWithFriend(val userId: String) : FriendListAction()
    data class UserClicked(val userId: String) : FriendListAction()
    data object NavigateBack : FriendListAction()
}