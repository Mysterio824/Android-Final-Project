package com.androidfinalproject.hacktok.ui.adminManage.postManagement

import androidx.lifecycle.ViewModel
import com.androidfinalproject.hacktok.model.MockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PostManagementViewModel : ViewModel() {
    private val _state = MutableStateFlow(PostManagementState())
    val state: StateFlow<PostManagementState> = _state.asStateFlow()

    init {
        _state.update { it.copy(posts = MockData.mockPosts) }
    }

    fun onAction(action: PostManagementAction) {
        when (action) {
            PostManagementAction.CreatePost -> {
                val newPost = MockData.mockPosts.first().copy(id = System.currentTimeMillis().toString())
                _state.update { it.copy(posts = it.posts + newPost, isCreatePostDialogOpen = false) }
            }
            is PostManagementAction.EditPost -> {
                _state.update { it.copy(
                    posts = it.posts.map { post ->
                        if (post.id == action.postId) post.copy(content = action.newContent) else post
                    },
                    isEditPostDialogOpen = false,
                    postToEdit = null
                ) }
            }
            is PostManagementAction.DeletePost -> {
                _state.update { it.copy(posts = it.posts.filter { it.id != action.postId }) }
            }
            PostManagementAction.OpenCreatePostDialog -> {
                _state.update { it.copy(isCreatePostDialogOpen = true) }
            }
            PostManagementAction.CloseCreatePostDialog -> {
                _state.update { it.copy(isCreatePostDialogOpen = false) }
            }
            is PostManagementAction.OpenEditPostDialog -> {
                _state.update { it.copy(isEditPostDialogOpen = true, postToEdit = action.post) }
            }
            PostManagementAction.CloseEditPostDialog -> {
                _state.update { it.copy(isEditPostDialogOpen = false, postToEdit = null) }
            }
        }
    }
}