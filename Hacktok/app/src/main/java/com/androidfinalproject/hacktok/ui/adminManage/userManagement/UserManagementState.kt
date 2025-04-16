package com.androidfinalproject.hacktok.ui.adminManage.userManagement

import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.enums.UserRole

data class UserManagementState (
    val availableRoles: List<UserRole> = emptyList(),
    val users: List<User> = emptyList(),
    val filteredUsers: List<User> = emptyList(),
)