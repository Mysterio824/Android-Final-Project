package com.androidfinalproject.hacktok.ui.chatDetail

sealed class ChatDetailAction {
    data object ToggleMute : ChatDetailAction()
    data object CreateGroup : ChatDetailAction()
    data object AddMember : ChatDetailAction()
    data object FindInChat : ChatDetailAction()
    data object DeleteChat : ChatDetailAction()
    data object BlockUser : ChatDetailAction()
    data object UnBlockUser : ChatDetailAction()
    data object LeaveGroup: ChatDetailAction()
    data class RenameGroup(val newName: String): ChatDetailAction()
    data object NavigateBack : ChatDetailAction()
    data object NavigateToUserProfile : ChatDetailAction()
}