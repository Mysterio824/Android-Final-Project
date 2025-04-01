package com.androidfinalproject.hacktok.ui.adminManage.postManagement

import com.androidfinalproject.hacktok.model.Post

data class PostManagementState(
    val posts: List<Post> = emptyList(),
    val selectedPost: List<Post> = emptyList(),
    val isCreatePostDialogOpen: Boolean = false,
    val isEditPostDialogOpen: Boolean = false,
    val postToEdit: Post? = null
)