package com.androidfinalproject.hacktok.ui.adminManage

sealed class AdminManagementAction {
    data class SelectTab(val tabIndex: String) : AdminManagementAction()
    object NavigateToStatistics: AdminManagementAction()
    object OnNavigateBack : AdminManagementAction()
}