package com.androidfinalproject.hacktok.ui.chat

sealed class ChatAction {
    data class SendMessage(val message: String) : ChatAction()
    data class DeleteMessage(val messageId: String) : ChatAction()
    data object LoadMoreMessages : ChatAction()
    data class NavigateToUserProfile(val userId: String) : ChatAction()
}