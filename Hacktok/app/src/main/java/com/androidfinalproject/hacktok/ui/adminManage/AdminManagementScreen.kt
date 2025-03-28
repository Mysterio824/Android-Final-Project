package com.androidfinalproject.hacktok.ui.adminManage.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.androidfinalproject.hacktok.ui.adminManage.AdminManagementAction
import com.androidfinalproject.hacktok.ui.adminManage.AdminManagementViewModel
import com.androidfinalproject.hacktok.ui.adminManage.components.UserManagementTab

@Composable
fun AdminManagementScreen(viewModel: AdminManagementViewModel = viewModel()) {
    val state = viewModel.state
    val tabs = listOf("Users", "Posts", "Comments", "Statistics")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "Admin Management",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Tabs
        TabRow(selectedTabIndex = state.selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = state.selectedTab == index,
                    onClick = { viewModel.onAction(AdminManagementAction.SelectTab(index)) }
                )
            }
        }

        // Tab Content
        when (state.selectedTab) {
            0 -> UserManagementTab(
                users = state.users,
                onUpdateRole = { userId, newRole ->
                    viewModel.onAction(AdminManagementAction.UpdateUserRole(userId, newRole))
                },
                onDelete = { userId ->
                    viewModel.onAction(AdminManagementAction.DeleteUser(userId))
                }
            )
            1 -> Post_management_tab(
                posts = state.posts,
                isCreateDialogOpen = state.isCreatePostDialogOpen,
                isEditDialogOpen = state.isEditPostDialogOpen,
                postToEdit = state.postToEdit,
                onOpenCreateDialog = {
                    viewModel.onAction(AdminManagementAction.OpenCreatePostDialog)
                },
                onCloseCreateDialog = {
                    viewModel.onAction(AdminManagementAction.CloseCreatePostDialog)
                },
                onCreatePost = { content ->
                    viewModel.onAction(AdminManagementAction.CreatePost(content))
                },
                onOpenEditDialog = { post ->
                    viewModel.onAction(AdminManagementAction.OpenEditPostDialog(post))
                },
                onCloseEditDialog = {
                    viewModel.onAction(AdminManagementAction.CloseEditPostDialog)
                },
                onEditPost = { postId, newContent ->
                    viewModel.onAction(AdminManagementAction.EditPost(postId, newContent))
                },
                onDeletePost = { postId ->
                    viewModel.onAction(AdminManagementAction.DeletePost(postId))
                }
            )
            2 -> CommentManagementTab(
                comments = state.comments,
                isEditDialogOpen = state.isEditCommentDialogOpen,
                commentToEdit = state.commentToEdit,
                onOpenEditDialog = { comment ->
                    viewModel.onAction(AdminManagementAction.OpenEditCommentDialog(comment))
                },
                onCloseEditDialog = {
                    viewModel.onAction(AdminManagementAction.CloseEditCommentDialog)
                },
                onEditComment = { commentId, newContent ->
                    viewModel.onAction(AdminManagementAction.EditComment(commentId, newContent))
                },
                onDeleteComment = { commentId ->
                    viewModel.onAction(AdminManagementAction.DeleteComment(commentId))
                }
            )
            3 -> StatisticsTab(
                users = state.users,
                posts = state.posts,
                comments = state.comments
            )
        }
    }
}