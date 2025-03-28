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
import com.androidfinalproject.hacktok.model.Post
import org.bson.types.ObjectId

@Composable
fun Post_management_tab(
    posts: List<Post>,
    isCreateDialogOpen: Boolean,
    isEditDialogOpen: Boolean,
    postToEdit: Post?,
    onOpenCreateDialog: () -> Unit,
    onCloseCreateDialog: () -> Unit,
    onCreatePost: (String) -> Unit,
    onOpenEditDialog: (Post) -> Unit,
    onCloseEditDialog: () -> Unit,
    onEditPost: (ObjectId?, String) -> Unit,
    onDeletePost: (ObjectId?) -> Unit
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
                onCreate = onCreatePost
            )
        }

        if (isEditDialogOpen && postToEdit != null) {
            EditPostDialog(
                currentContent = postToEdit.content,
                onDismiss = onCloseEditDialog,
                onSave = { newContent -> onEditPost(postToEdit.id, newContent) }
            )
        }

        LazyColumn {
            items(posts) { post ->
                PostItem(
                    post = post,
                    onEdit = { onOpenEditDialog(post) },
                    onDelete = { onDeletePost(post.id) }
                )
            }
        }
    }
}

@Composable
fun CreatePostDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Post") },
        text = {
            TextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Post Content") }
            )
        },
        confirmButton = {
            Button(onClick = { onCreate(content) }) {
                Text("Create")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
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
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Content: ${post.content}", fontWeight = FontWeight.Bold)
                Text(text = "Posted by: ${post.user.username}")
                Text(text = "Likes: ${post.likeCount}")
            }

            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Post")
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Post", tint = Color.Red)
            }
        }
    }
}

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
            TextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Post Content") }
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