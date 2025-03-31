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
import com.androidfinalproject.hacktok.model.Post

@Composable
fun PostManagementTab(
    posts: List<Post>,
    isCreateDialogOpen: Boolean,
    isEditDialogOpen: Boolean,
    postToEdit: Post?,
    onOpenCreateDialog: () -> Unit,
    onCloseCreateDialog: () -> Unit,
    onCreatePost: () -> Unit,
    onOpenEditDialog: (Post) -> Unit,
    onCloseEditDialog: () -> Unit,
    onEditPost: (String, String) -> Unit,
    onDeletePost: (String) -> Unit
) {
    Column {
        Button(
            onClick = onOpenCreateDialog,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Create New Post")
        }

        if (isCreateDialogOpen) {
            CreatePostDialog(
                onDismiss = onCloseCreateDialog,
                onCreate = { onCreatePost() }
            )
        }

        if (isEditDialogOpen && postToEdit != null) {
            EditPostDialog(
                currentContent = postToEdit.content,
                onDismiss = onCloseEditDialog,
                onSave = { newContent -> onEditPost(postToEdit.id ?: "", newContent) }
            )
        }

        LazyColumn {
            items(posts) { post ->
                PostItem(
                    post = post,
                    onEdit = { onOpenEditDialog(post) },
                    onDelete = { onDeletePost(post.id ?: "") }
                )
            }
        }
    }
}

@Composable
fun CreatePostDialog(
    onDismiss: () -> Unit,
    onCreate: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Post") },
        text = { Text("New post will be created with default mock data") },
        confirmButton = {
            TextButton(onClick = { onCreate() }) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun PostItem(
    post: Post,
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
                    text = "Content: ${post.content}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Posted by: ${post.userId}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Likes: ${post.likeCount}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit Post",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Post",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostDialog(
    currentContent: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var content by remember { mutableStateOf(currentContent) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Post") },
        text = {
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Post Content") },
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