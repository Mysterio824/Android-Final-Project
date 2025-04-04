package com.androidfinalproject.hacktok.ui.chat

sealed class ChatAction {
    data class SendMessage(val message: String) : ChatAction()
    data class DeleteMessage(val messageId: String?) : ChatAction()
    data object LoadInitialMessages : ChatAction()
    data object NavigateBack : ChatAction()
    data class NavigateToManageUser(val userId : String?) : ChatAction()
}