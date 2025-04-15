package com.androidfinalproject.hacktok.ui.adminManage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.activity.compose.BackHandler
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.androidfinalproject.hacktok.ui.adminManage.commentManagement.*
import com.androidfinalproject.hacktok.ui.adminManage.component.AdminTabBar
import com.androidfinalproject.hacktok.ui.adminManage.postManagement.PostManagementTabRoot
import com.androidfinalproject.hacktok.ui.adminManage.postManagement.PostManagementViewModel
import com.androidfinalproject.hacktok.ui.adminManage.reportManagement.ReportManagementTabRoot
import com.androidfinalproject.hacktok.ui.adminManage.reportManagement.ReportManagementViewModel
import com.androidfinalproject.hacktok.ui.adminManage.userManagement.UserManagementScreen
import com.androidfinalproject.hacktok.ui.adminManage.userManagement.UserManagementViewModel
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@Composable
fun AdminManagementScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val userManagementViewModel: UserManagementViewModel = hiltViewModel()

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("User Management") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Content Management") }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
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
            1 -> ContentManagementScreen()
            2 -> ReportsScreen()
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