package com.androidfinalproject.hacktok.ui.chat

import com.androidfinalproject.hacktok.model.Chat

sealed class ChatAction {
    data class SendMessage(val message: String) : ChatAction()
    data class SendImage(val imageUri: String) : ChatAction()
    data class DeleteMessage(val messageId: String?) : ChatAction()
    data object LoadInitialMessages : ChatAction()
    data object ToggleMute : ChatAction()
    data class ChatOptionNavigate(val chatId: String) : ChatAction()
    data object CreateGroup : ChatAction()
    data object DeleteChat : ChatAction()
    data object BlockUser : ChatAction()
    data object NavigateBack : ChatAction()
    data class NavigateToManageUser(val userId : String?) : ChatAction()

    data class SetSearchMode(val enabled: Boolean) : ChatAction()
    data class UpdateSearchQuery(val query: String) : ChatAction()
    data object SearchNext : ChatAction()
    data object SearchPrevious : ChatAction()
}