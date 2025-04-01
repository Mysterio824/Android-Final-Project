package com.androidfinalproject.hacktok.ui.adminManage.commentManagement

import androidx.lifecycle.ViewModel
import com.androidfinalproject.hacktok.model.MockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CommentManagementViewModel : ViewModel() {
    private val _state = MutableStateFlow(CommentManagementState())
    val state: StateFlow<CommentManagementState> = _state.asStateFlow()

    init {
        _state.update { it.copy(comments = MockData.mockComments) }
    }

    fun onAction(action: CommentManagementAction) {
        when (action) {
            is CommentManagementAction.EditComment -> {
                _state.update { it.copy(
                    comments = it.comments.map { comment ->
                        if (comment.id == action.commentId) comment.copy(content = action.newContent) else comment
                    },
                    isEditCommentDialogOpen = false,
                    commentToEdit = null
                ) }
            }
            is CommentManagementAction.DeleteComment -> {
                _state.update { it.copy(comments = it.comments.filter { it.id != action.commentId }) }
            }
            is CommentManagementAction.OpenEditCommentDialog -> {
                _state.update { it.copy(isEditCommentDialogOpen = true, commentToEdit = action.comment) }
            }
            CommentManagementAction.CloseEditCommentDialog -> {
                _state.update { it.copy(isEditCommentDialogOpen = false, commentToEdit = null) }
            }
        }
    }
}