package com.androidfinalproject.hacktok.ui.adminManage.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.model.Comment

@Composable
fun StatisticCard(title: String, count: Int, color: Color = Color.Blue) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            androidx.compose.material3.Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            androidx.compose.material3.Text(
                text = count.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

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
    Column (
        modifier = Modifier
            .padding(16.dp)
    ) {
        // Statistic Cards
        StatisticCard(title = "Comments Today", count = 29)
        StatisticCard(title = "Comments This Month", count = 144)
        StatisticCard(title = "Comments This Year", count = 23411)
        StatisticCard(title = "Banned Comments", count = 776, color = Color.Red)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
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
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Comment: ${comment.content}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "By: ${comment.userId}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Likes: ${comment.likeCount}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit Comment",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Comment",
                    tint = MaterialTheme.colorScheme.error
                )
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
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Comment Content") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(content) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}