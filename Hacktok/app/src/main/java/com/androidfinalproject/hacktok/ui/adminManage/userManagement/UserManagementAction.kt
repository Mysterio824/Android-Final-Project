package com.androidfinalproject.hacktok.ui.adminManage.userManagement

import com.androidfinalproject.hacktok.model.UserRole

sealed class UserManagementAction {
    data class UpdateUserRole(val userId: String, val newRole: UserRole) : UserManagementAction()
    data class DeleteUser(val userId: String) : UserManagementAction()
    data class FilterUsers(val query: String) : UserManagementAction()
}