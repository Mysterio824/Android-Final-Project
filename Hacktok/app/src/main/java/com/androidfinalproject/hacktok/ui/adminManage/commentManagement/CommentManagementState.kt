package com.androidfinalproject.hacktok.ui.adminManage.commentManagement

import com.androidfinalproject.hacktok.model.Comment

data class CommentManagementState(
    val comments: List<Comment> = emptyList(),
    val isEditCommentDialogOpen: Boolean = false,
    val commentToEdit: Comment? = null
)