package com.androidfinalproject.hacktok.ui.adminManage.postManagement

import com.androidfinalproject.hacktok.model.Post

sealed class PostManagementAction {
    object CreatePost : PostManagementAction()
    data class EditPost(val postId: String, val newContent: String) : PostManagementAction()
    data class DeletePost(val postId: String) : PostManagementAction()
    object OpenCreatePostDialog : PostManagementAction()
    object CloseCreatePostDialog : PostManagementAction()
    data class OpenEditPostDialog(val post: Post) : PostManagementAction()
    object CloseEditPostDialog : PostManagementAction()
}