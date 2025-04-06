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
import com.androidfinalproject.hacktok.ui.adminManage.commentManagement.*
import com.androidfinalproject.hacktok.ui.adminManage.postManagement.PostManagementTabRoot
import com.androidfinalproject.hacktok.ui.adminManage.postManagement.PostManagementViewModel
import com.androidfinalproject.hacktok.ui.adminManage.reportManagement.ReportManagementTabRoot
import com.androidfinalproject.hacktok.ui.adminManage.reportManagement.ReportManagementViewModel
import com.androidfinalproject.hacktok.ui.adminManage.userManagement.UserManagementTabRoot
import com.androidfinalproject.hacktok.ui.adminManage.userManagement.UserManagementViewModel

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
            0 -> UserManagementTabRoot(
                viewModel = UserManagementViewModel()
            )
            1 -> PostManagementTabRoot(
                viewModel = PostManagementViewModel()
            )
            2 -> CommentManagementTabRoot(
                viewModel = CommentManagementViewModel()
            )
            3 -> ReportManagementTabRoot(
                viewModel = ReportManagementViewModel()
            )
        }
    }
}