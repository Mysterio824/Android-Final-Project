package com.androidfinalproject.hacktok.ui.groupChat

sealed class GroupChatAction {
    data class SendMessage(val message: String) : GroupChatAction()
    data class DeleteMessage(val messageId: String?) : GroupChatAction()
    data object LoadInitialMessages : GroupChatAction()
    data object ToggleMute : GroupChatAction()
    data class RenameGroup(val newName: String) : GroupChatAction()
    data object FindInChat : GroupChatAction()
    data object LeaveGroup : GroupChatAction()
    data object NavigateBack : GroupChatAction()
    data class NavigateToManageGroup(val groupId: String?) : GroupChatAction()
}
