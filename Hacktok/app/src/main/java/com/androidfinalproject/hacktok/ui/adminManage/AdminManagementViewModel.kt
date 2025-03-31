package com.androidfinalproject.hacktok.ui.adminManage

import androidx.lifecycle.ViewModel
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.Comment
import com.androidfinalproject.hacktok.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AdminManagementViewModel : ViewModel() {
    private val _state = MutableStateFlow(AdminManagementState(
        users = MockData.mockUsers,
        filteredUsers = MockData.mockUsers,
        posts = MockData.mockPosts,
        comments = MockData.mockComments,
        availableRoles = MockData.mockUserRoles
    ))
    val state: StateFlow<AdminManagementState> = _state.asStateFlow()

    fun onAction(action: AdminManagementAction) {
        when (action) {
            is AdminManagementAction.UpdateUserRole -> {
                _state.update { currentState ->
                    currentState.copy(
                        users = currentState.users.map { user ->
                            if (user.id?.equals(action.userId) == true) {
                                user.copy(role = action.newRole)
                            } else {
                                user
                            }
                        },
                        filteredUsers = currentState.filteredUsers.map { user ->
                            if (user.id?.equals(action.userId) == true) {
                                user.copy(role = action.newRole)
                            } else {
                                user
                            }
                        }
                    )
                }
            }
            is AdminManagementAction.DeleteUser -> {
                _state.update { currentState ->
                    currentState.copy(
                        users = currentState.users.filter { it.id?.equals(action.userId) == false },
                        filteredUsers = currentState.filteredUsers.filter { it.id?.equals(action.userId) == false }
                    )
                }
            }
            is AdminManagementAction.CreatePost -> {
                val newPost = MockData.mockPosts.first().copy(id = System.currentTimeMillis().toString())
                _state.update { currentState ->
                    currentState.copy(
                        posts = currentState.posts + newPost,
                        isCreatePostDialogOpen = false
                    )
                }
            }
            is AdminManagementAction.EditPost -> {
                _state.update { currentState ->
                    currentState.copy(
                        posts = currentState.posts.map { post ->
                            if (post.id?.equals(action.postId) == true) post.copy(content = action.newContent) else post
                        },
                        isEditPostDialogOpen = false,
                        postToEdit = null
                    )
                }
            }
            is AdminManagementAction.DeletePost -> {
                _state.update { currentState ->
                    currentState.copy(
                        posts = currentState.posts.filter { it.id?.equals(action.postId) == false }
                    )
                }
            }
            is AdminManagementAction.EditComment -> {
                _state.update { currentState ->
                    currentState.copy(
                        comments = currentState.comments.map { comment ->
                            if (comment.id?.equals(action.commentId) == true)
                                comment.copy(content = action.newContent)
                            else comment
                        },
                        isEditCommentDialogOpen = false,
                        commentToEdit = null
                    )
                }
            }
            is AdminManagementAction.DeleteComment -> {
                _state.update { currentState ->
                    currentState.copy(
                        comments = currentState.comments.filter { it.id?.equals(action.commentId) == false }
                    )
                }
            }
            is AdminManagementAction.SelectTab -> {
                _state.update { currentState ->
                    currentState.copy(selectedTab = action.tabIndex)
                }
            }
            is AdminManagementAction.OpenCreatePostDialog -> {
                _state.update { currentState ->
                    currentState.copy(isCreatePostDialogOpen = true)
                }
            }
            is AdminManagementAction.CloseCreatePostDialog -> {
                _state.update { currentState ->
                    currentState.copy(isCreatePostDialogOpen = false)
                }
            }
            is AdminManagementAction.OpenEditPostDialog -> {
                _state.update { currentState ->
                    currentState.copy(isEditPostDialogOpen = true, postToEdit = action.post)
                }
            }
            is AdminManagementAction.CloseEditPostDialog -> {
                _state.update { currentState ->
                    currentState.copy(isEditPostDialogOpen = false, postToEdit = null)
                }
            }
            is AdminManagementAction.OpenEditCommentDialog -> {
                _state.update { currentState ->
                    currentState.copy(isEditCommentDialogOpen = true, commentToEdit = action.comment)
                }
            }
            is AdminManagementAction.CloseEditCommentDialog -> {
                _state.update { currentState ->
                    currentState.copy(isEditCommentDialogOpen = false, commentToEdit = null)
                }
            }
            is AdminManagementAction.FilterUsers -> {
                _state.update { currentState ->
                    currentState.copy(
                        filteredUsers = if (action.query.isBlank()) {
                            currentState.users
                        } else {
                            currentState.users.filter { user ->
                                user.username.contains(action.query, ignoreCase = true) ||
                                        user.email.contains(action.query, ignoreCase = true) ||
                                        user.fullName?.contains(action.query, ignoreCase = true) == true
                            }
                        }
                    )
                }
            }
        }
    }
}