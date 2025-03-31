package com.androidfinalproject.hacktok.ui.adminManage.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.Comment

@Composable
fun CommentManagementTab(
    comments: List<Comment>,
    isEditDialogOpen: Boolean,
    commentToEdit: Comment?,
    onOpenEditDialog: (Comment) -> Unit,
    onCloseEditDialog: () -> Unit,
    onEditComment: (String, String) -> Unit,
    onDeleteComment: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        items(comments) { comment ->
            CommentItem(
                comment = comment,
                onEdit = { onOpenEditDialog(comment) },
                onDelete = { onDeleteComment(comment.id ?: "") }
            )
        }
    }

    if (isEditDialogOpen && commentToEdit != null) {
        EditCommentDialog(
            currentContent = commentToEdit.content,
            onDismiss = onCloseEditDialog,
            onSave = { newContent -> onEditComment(commentToEdit.id ?: "", newContent) }
        )
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Comment: ${comment.content}", fontWeight = FontWeight.Bold)
                Text(text = "By: ${comment.userId}")
                Text(text = "Likes: ${comment.likeCount}")
            }

            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Comment")
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Comment", tint = Color.Red)
            }
        }
    }
}

@Composable
fun EditCommentDialog(
    currentContent: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var content by remember { mutableStateOf(currentContent) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Comment") },
        text = {
            TextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Comment Content") }
            )
        },
        confirmButton = {
            Button(onClick = { onSave(content) }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}