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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.activity.compose.BackHandler
import com.androidfinalproject.hacktok.ui.adminManage.commentManagement.*
import com.androidfinalproject.hacktok.ui.adminManage.component.AdminTabBar
import com.androidfinalproject.hacktok.ui.adminManage.postManagement.PostManagementTabRoot
import com.androidfinalproject.hacktok.ui.adminManage.postManagement.PostManagementViewModel
import com.androidfinalproject.hacktok.ui.adminManage.reportManagement.ReportManagementTabRoot
import com.androidfinalproject.hacktok.ui.adminManage.reportManagement.ReportManagementViewModel
import com.androidfinalproject.hacktok.ui.adminManage.userManagement.UserManagementTabRoot
import com.androidfinalproject.hacktok.ui.adminManage.userManagement.UserManagementViewModel
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@Composable
fun AdminManagementScreen(
    state: AdminManagementState,
    onAction: (AdminManagementAction) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onAction(AdminManagementAction.OnNavigateBack)
    }

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
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = "View Statistics",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        AdminTabBar(
            currentScreen = state.selectedTab,
            onItemSelected = { tab -> onAction(AdminManagementAction.SelectTab(tab)) }
        )

        when (state.selectedTab) {
            "Users" -> UserManagementTabRoot(
                viewModel = UserManagementViewModel()
            )
            "Posts" -> PostManagementTabRoot(
                viewModel = PostManagementViewModel()
            )
            "Comments" -> CommentManagementTabRoot(
                viewModel = CommentManagementViewModel()
            )
            "Reports" -> ReportManagementTabRoot(
                viewModel = ReportManagementViewModel()
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AdminManagementScreenPreview() {
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            AdminManagementScreen(
                state = AdminManagementState(
                    selectedTab = "Posts"
                ),
                onAction = {},
                modifier = Modifier
            )
        }
    }
}