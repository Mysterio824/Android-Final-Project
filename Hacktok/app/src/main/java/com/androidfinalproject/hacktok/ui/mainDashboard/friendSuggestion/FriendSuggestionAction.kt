package com.androidfinalproject.hacktok.ui.mainDashboard.friendSuggestion

sealed class FriendSuggestionAction {
    data class HandleRequest(val userId: String, val isAccepted: Boolean) : FriendSuggestionAction()
    data class SendRequest(val userId: String): FriendSuggestionAction()
    data class UnSendRequest(val userId: String): FriendSuggestionAction()
    data class OnUserClick(val userId: String): FriendSuggestionAction()
    data class OnRemove(val userId: String): FriendSuggestionAction()
    data object OnFriendListNavigate : FriendSuggestionAction()
    data object Refresh : FriendSuggestionAction()
}