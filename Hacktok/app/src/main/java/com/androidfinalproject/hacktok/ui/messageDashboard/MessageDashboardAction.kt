package com.androidfinalproject.hacktok.ui.messageDashboard

sealed class MessageDashboardAction {
    data class SearchQueryChanged(val query: String) : MessageDashboardAction()
    data class GoToChat(val userId: String?) : MessageDashboardAction()
    data class GoToNewChat(val userId: String?) : MessageDashboardAction()
    data class GoToNewGroupChat(val groupId: String?) : MessageDashboardAction()
    data object NewChat : MessageDashboardAction()
    data object NewGroup : MessageDashboardAction()
    data class DeleteChat(val chatId: String): MessageDashboardAction()
    data class BlockChat(val userId: String): MessageDashboardAction()
    data class UnBlockChat(val userId: String): MessageDashboardAction()
    data class MuteChat(val chatId: String): MessageDashboardAction()
    object OnNavigateBack : MessageDashboardAction()
    object Refresh : MessageDashboardAction()
}