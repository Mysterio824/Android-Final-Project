package com.androidfinalproject.hacktok.ui.adminManage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.adminManage.components.*

@Composable
fun AdminManagementScreen(
    state: AdminManagementState,
    onAction: (AdminManagementAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf("Users", "Posts", "Comments", "Reports")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Admin Management",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(
                onClick = { onAction(AdminManagementAction.NavigateToStatistics) },
                modifier = Modifier.size(36.dp) // Compact size for balance
            ) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = "View Statistics",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        TabRow(
            selectedTabIndex = state.selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = state.selectedTab == index,
                    onClick = { onAction(AdminManagementAction.SelectTab(index)) },
                    modifier = Modifier
                        .weight(1f) // Distribute space evenly
                        .widthIn(min = 100.dp) // Minimum width to fit "Comments"
                )
            }
        }

        when (state.selectedTab) {
            0 -> UserManagementTab(
                users = state.filteredUsers,
                onUpdateRole = { userId, newRole ->
                    onAction(AdminManagementAction.UpdateUserRole(userId, newRole))
                },
                onDelete = { userId ->
                    onAction(AdminManagementAction.DeleteUser(userId))
                },
                onFilterUsers = { query ->
                    onAction(AdminManagementAction.FilterUsers(query))
                }
            )
            1 -> PostManagementTab(
                posts = state.posts,
                isCreateDialogOpen = state.isCreatePostDialogOpen,
                isEditDialogOpen = state.isEditPostDialogOpen,
                postToEdit = state.postToEdit,
                onOpenCreateDialog = {
                    onAction(AdminManagementAction.OpenCreatePostDialog)
                },
                onCloseCreateDialog = {
                    onAction(AdminManagementAction.CloseCreatePostDialog)
                },
                onCreatePost = {
                    onAction(AdminManagementAction.CreatePost)
                },
                onOpenEditDialog = { post ->
                    onAction(AdminManagementAction.OpenEditPostDialog(post))
                },
                onCloseEditDialog = {
                    onAction(AdminManagementAction.CloseEditPostDialog)
                },
                onEditPost = { postId, newContent ->
                    onAction(AdminManagementAction.EditPost(postId, newContent))
                },
                onDeletePost = { postId ->
                    onAction(AdminManagementAction.DeletePost(postId))
                }
            )
            2 -> CommentManagementTab(
                comments = state.comments,
                isEditDialogOpen = state.isEditCommentDialogOpen,
                commentToEdit = state.commentToEdit,
                onOpenEditDialog = { comment ->
                    onAction(AdminManagementAction.OpenEditCommentDialog(comment))
                },
                onCloseEditDialog = {
                    onAction(AdminManagementAction.CloseEditCommentDialog)
                },
                onEditComment = { commentId, newContent ->
                    onAction(AdminManagementAction.EditComment(commentId, newContent))
                },
                onDeleteComment = { commentId ->
                    onAction(AdminManagementAction.DeleteComment(commentId))
                }
            )
            3 -> ReportManagementTab(
                reports = state.reports,
                reportCounts = state.reportCounts,
                isBanDialogOpen = state.isBanUserDialogOpen,
                isResolveDialogOpen = state.isResolveReportDialogOpen,
                selectedReport = state.selectedReport,
                onOpenBanDialog = { report ->
                    onAction(AdminManagementAction.OpenBanUserDialog(report))
                },
                onCloseBanDialog = {
                    onAction(AdminManagementAction.CloseBanUserDialog)
                },
                onOpenResolveDialog = { report ->
                    onAction(AdminManagementAction.OpenResolveReportDialog(report))
                },
                onCloseResolveDialog = {
                    onAction(AdminManagementAction.CloseResolveReportDialog)
                },
                onBanUser = { userId, isPermanent, durationDays ->
                    onAction(AdminManagementAction.BanUser(userId, isPermanent, durationDays))
                },
                onDeleteContent = { contentId, contentType ->
                    when (contentType) {
                        "post" -> onAction(AdminManagementAction.DeletePost(contentId))
                        "comment" -> onAction(AdminManagementAction.DeleteComment(contentId))
                        else -> { /* Do nothing for unsupported types */ }
                    }
                },
                onResolveReport = { reportId, resolutionNote ->
                    onAction(AdminManagementAction.ResolveReport(reportId, resolutionNote))
                }
            )
        }
    }
}