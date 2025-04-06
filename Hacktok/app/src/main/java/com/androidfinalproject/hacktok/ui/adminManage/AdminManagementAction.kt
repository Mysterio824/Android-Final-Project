package com.androidfinalproject.hacktok.ui.adminManage

sealed class AdminManagementAction {
    data class SelectTab(val tabIndex: Int) : AdminManagementAction()
    object NavigateToStatistics: AdminManagementAction()
}