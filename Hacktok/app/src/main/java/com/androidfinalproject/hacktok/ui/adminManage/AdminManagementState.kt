package com.androidfinalproject.hacktok.ui.adminManage

data class AdminManagementState(
    val selectedTab: Int = 0,
)

enum class Site{
    Users,
    Posts,
    Comments,
    Reports
}