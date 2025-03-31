package com.androidfinalproject.hacktok.ui.messageDashboard

sealed class MessageDashboardAction {
    data class SearchQueryChanged(val query: String) : MessageDashboardAction()
    data class GoToChat(val userId: String?) : MessageDashboardAction()
    data object NewChat : MessageDashboardAction()
    data object NewGroup : MessageDashboardAction()
    data object NavigateBack : MessageDashboardAction()
}