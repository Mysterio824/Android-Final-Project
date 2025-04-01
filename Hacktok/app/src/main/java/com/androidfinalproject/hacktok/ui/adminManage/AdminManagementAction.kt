package com.androidfinalproject.hacktok.ui.adminManage

import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.Comment
import com.androidfinalproject.hacktok.model.Report
import com.androidfinalproject.hacktok.model.UserRole

sealed class AdminManagementAction {
    data class UpdateUserRole(val userId: String, val newRole: UserRole) : AdminManagementAction()
    data class DeleteUser(val userId: String) : AdminManagementAction()

    object CreatePost : AdminManagementAction()
    data class EditPost(val postId: String, val newContent: String) : AdminManagementAction()
    data class DeletePost(val postId: String) : AdminManagementAction()

    data class EditComment(val commentId: String, val newContent: String) : AdminManagementAction()
    data class DeleteComment(val commentId: String) : AdminManagementAction()

    data class SelectTab(val tabIndex: Int) : AdminManagementAction()

    object OpenCreatePostDialog : AdminManagementAction()
    object CloseCreatePostDialog : AdminManagementAction()

    data class OpenEditPostDialog(val post: Post) : AdminManagementAction()
    object CloseEditPostDialog : AdminManagementAction()

    data class OpenEditCommentDialog(val comment: Comment) : AdminManagementAction()
    object CloseEditCommentDialog : AdminManagementAction()

    data class FilterUsers(val query: String) : AdminManagementAction()

    object NavigateToStatistics: AdminManagementAction()

    data class ResolveReport(val reportId: String, val resolutionNote: String) : AdminManagementAction()
    data class BanUser(val userId: String, val isPermanent: Boolean, val durationDays: Int?) : AdminManagementAction()
    object CloseResolveReportDialog : AdminManagementAction()
    data class OpenResolveReportDialog(val report: Report) : AdminManagementAction()
    object CloseBanUserDialog : AdminManagementAction()
    data class OpenBanUserDialog(val report: Report) : AdminManagementAction()
}