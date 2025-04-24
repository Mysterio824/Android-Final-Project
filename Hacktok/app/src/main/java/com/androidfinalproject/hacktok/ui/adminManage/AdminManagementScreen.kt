package com.androidfinalproject.hacktok.ui.adminManage

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.androidfinalproject.hacktok.ui.adminManage.postManagement.PostManagementViewModel
import com.androidfinalproject.hacktok.ui.adminManage.reportManagement.ReportManagementTab
import com.androidfinalproject.hacktok.ui.adminManage.reportManagement.ReportManagementViewModel
import com.androidfinalproject.hacktok.ui.adminManage.userManagement.UserManagementScreen
import com.androidfinalproject.hacktok.ui.adminManage.userManagement.UserManagementViewModel
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme
import kotlinx.coroutines.selects.select

@Composable
fun AdminManagementScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    onAction: (AdminManagementAction) -> Unit = {},
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val userManagementViewModel: UserManagementViewModel = hiltViewModel()
    val postManagementViewModel: PostManagementViewModel = hiltViewModel()
    val reportManagementViewModel: ReportManagementViewModel = hiltViewModel()

    val reportState by reportManagementViewModel.state.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = {
                    selectedTab = 0
                    onAction(AdminManagementAction.SelectTab("Users"))
                },
                text = { Text("User Management") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = {
                    selectedTab = 1
                    onAction(AdminManagementAction.SelectTab("Content"))
                },
                text = { Text("Content Management") }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = {
                    selectedTab = 2
                    onAction(AdminManagementAction.SelectTab("Reports"))
                },
                text = { Text("Reports") }
            )
        }

        when (selectedTab) {
            0 -> UserManagementScreen(
                viewModel = userManagementViewModel,
                onUserClick = { userId -> 
                    // Navigate to user detail screen
                    navController.navigate("userDetail/$userId")
                }
            )
            1 -> ContentManagementScreen(

            )
            2 -> ReportManagementTab(
                state = reportState,
                onAction = { action -> reportManagementViewModel.onAction(action) }
            )
        }
    }
}

@Composable
fun ContentManagementScreen() {
    // Implement content management screen
    Text("Content Management")
}

@Composable
fun ReportsScreen() {
    // Implement reports screen
    Text("Reports")
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
            val navController = rememberNavController()
            AdminManagementScreen(
                navController = navController,
                modifier = Modifier
            )
        }
    }
}