package com.androidfinalproject.hacktok.ui.adminManage

import com.androidfinalproject.hacktok.model.UserRole
import org.bson.types.ObjectId

sealed class AdminManagementAction {
    data class UpdateUserRole(val userId: ObjectId?, val newRole: UserRole) : AdminManagementAction()
    data class DeleteUser(val userId: ObjectId?) : AdminManagementAction()
    data class CreatePost(val content: String) : AdminManagementAction()
    data class EditPost(val postId: ObjectId?, val newContent: String) : AdminManagementAction()
    data class DeletePost(val postId: ObjectId?) : AdminManagementAction()
    data class EditComment(val commentId: ObjectId?, val newContent: String) : AdminManagementAction()
    data class DeleteComment(val commentId: ObjectId?) : AdminManagementAction()
    data class SelectTab(val tabIndex: Int) : AdminManagementAction()
    object OpenCreatePostDialog : AdminManagementAction()
    object CloseCreatePostDialog : AdminManagementAction()
    data class OpenEditPostDialog(val post: com.androidfinalproject.hacktok.model.Post) : AdminManagementAction()
    object CloseEditPostDialog : AdminManagementAction()
    data class OpenEditCommentDialog(val comment: com.androidfinalproject.hacktok.model.Comment) : AdminManagementAction()
    object CloseEditCommentDialog : AdminManagementAction()
}