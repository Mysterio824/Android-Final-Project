package com.androidfinalproject.hacktok.ui.adminManage.postManagement

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.adminManage.postManagement.component.*

@Composable
fun PostManagementTab(
    state: PostManagementState,
    onAction: (PostManagementAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp) // Uniform padding
    ) {
        Button(
            onClick = { onAction(PostManagementAction.OpenCreatePostDialog) },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Create New Post")
        }

        Spacer(modifier = Modifier.height(16.dp)) // Consistent spacing

        // Create Post Dialog
        if (state.isCreatePostDialogOpen) {
            CreatePostDialog(
                onDismiss = { onAction(PostManagementAction.CloseCreatePostDialog) },
                onCreate = { onAction(PostManagementAction.CreatePost) }
            )
        }

        // Edit Post Dialog
        if (state.isEditPostDialogOpen && state.postToEdit != null) {
            EditPostDialog(
                currentContent = state.postToEdit!!.content,
                onDismiss = { onAction(PostManagementAction.CloseEditPostDialog) },
                onSave = { newContent ->
                    onAction(PostManagementAction.EditPost(state.postToEdit!!.id ?: "", newContent))
                }
            )
        }

        // Post List or Empty State
        if (state.posts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No posts available",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn {
                items(state.posts) { post ->
                    PostItem(
                        post = post,
                        onEdit = { onAction(PostManagementAction.OpenEditPostDialog(post)) },
                        onDelete = { onAction(PostManagementAction.DeletePost(post.id ?: "")) }
                    )
                }
            }
        }
    }
}