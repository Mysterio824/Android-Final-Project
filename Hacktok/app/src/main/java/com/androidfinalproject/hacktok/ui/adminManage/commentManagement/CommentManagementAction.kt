package com.androidfinalproject.hacktok.ui.adminManage.commentManagement

import com.androidfinalproject.hacktok.model.Comment

sealed class CommentManagementAction {
    data class EditComment(val commentId: String, val newContent: String) : CommentManagementAction()
    data class DeleteComment(val commentId: String) : CommentManagementAction()
    data class OpenEditCommentDialog(val comment: Comment) : CommentManagementAction()
    object CloseEditCommentDialog : CommentManagementAction()
}