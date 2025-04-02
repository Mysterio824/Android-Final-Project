package com.androidfinalproject.hacktok.ui.adminManage.commentManagement

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.adminManage.commentManagement.component.*

@Composable
fun CommentManagementTab(
    state: CommentManagementState,
    onAction: (CommentManagementAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (state.comments.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No comments available",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn {
                items(state.comments) { comment ->
                    CommentItem(
                        comment = comment,
                        onEdit = { onAction(CommentManagementAction.OpenEditCommentDialog(comment)) },
                        onDelete = { cmtId ->
                            onAction(CommentManagementAction.DeleteComment(cmtId!!))
                        }
                    )
                }
            }
        }

        if (state.isEditCommentDialogOpen && state.commentToEdit != null) {
            EditCommentDialog(
                currentContent = state.commentToEdit!!.content,
                onDismiss = { onAction(CommentManagementAction.CloseEditCommentDialog) },
                onSave = { newContent ->
                    onAction(
                        CommentManagementAction.EditComment(state.commentToEdit!!.id ?: "", newContent)
                    )
                }
            )
        }
    }
}