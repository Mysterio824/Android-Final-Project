package com.androidfinalproject.hacktok.ui.adminManage

import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.Comment
import com.androidfinalproject.hacktok.model.Report
import com.androidfinalproject.hacktok.model.UserRole

data class AdminManagementState(
    val users: List<User> = emptyList(),
    val filteredUsers: List<User> = emptyList(),
    val posts: List<Post> = emptyList(),
    val comments: List<Comment> = emptyList(),
    val reports: List<Report> = emptyList(),
    val reportCounts: Map<String, Int> = emptyMap(), // Counts of reports per target
    val availableRoles: List<UserRole> = emptyList(),
    val selectedTab: Int = 0,
    val isCreatePostDialogOpen: Boolean = false,
    val isEditPostDialogOpen: Boolean = false,
    val postToEdit: Post? = null,
    val isEditCommentDialogOpen: Boolean = false,
    val commentToEdit: Comment? = null,
    val isBanUserDialogOpen: Boolean = false,
    val isResolveReportDialogOpen: Boolean = false,
    val selectedReport: Report? = null
)

enum class Site{
    Users,
    Posts,
    Comments,
    Reports
}