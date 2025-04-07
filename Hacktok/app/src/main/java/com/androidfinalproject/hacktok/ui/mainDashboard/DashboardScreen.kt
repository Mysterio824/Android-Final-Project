package com.androidfinalproject.hacktok.ui.mainDashboard

import SearchDashboardScreenRoot
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import com.androidfinalproject.hacktok.ui.mainDashboard.component.BottomNavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.mainDashboard.currentProfile.CurrentProfileScreenRoot
import com.androidfinalproject.hacktok.ui.mainDashboard.currentProfile.CurrentProfileViewModel
import com.androidfinalproject.hacktok.ui.mainDashboard.home.HomeScreenRoot
import com.androidfinalproject.hacktok.ui.mainDashboard.home.HomeScreenViewModel
import com.androidfinalproject.hacktok.ui.mainDashboard.messageDashboard.MessageDashboardRoot
import com.androidfinalproject.hacktok.ui.mainDashboard.messageDashboard.MessageDashboardViewModel
import com.androidfinalproject.hacktok.ui.mainDashboard.search.SearchViewModel
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme
import androidx.activity.compose.BackHandler


@Composable
fun DashboardScreen(
    state: DashboardState,
    onAction: (DashboardAction) -> Unit
) {
    BackHandler {
        onAction(DashboardAction.OnNavigateBack)
    }
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentScreen = state.selectedTab,
                onItemSelected = { tab -> onAction(DashboardAction.SelectTab(tab)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state.selectedTab) {
                "Home" -> {
                    HomeScreenRoot(
                        viewModel = HomeScreenViewModel(),
                        onUserClick = { id -> onAction(DashboardAction.OnUserClick(id)) },
                        onPostClick = { id -> onAction(DashboardAction.OnPostClick(id)) }
                    )
                }

                "Search" -> {
                    SearchDashboardScreenRoot(
                        viewModel = SearchViewModel(),
                        onUserClick = { id -> onAction(DashboardAction.OnUserClick(id!!)) },
                        onPostClick = { id -> onAction(DashboardAction.OnPostClick(id!!)) }
                    )
                }

                "Chat" -> {
                    MessageDashboardRoot(
                        viewModel = MessageDashboardViewModel(),
                        onNewChat = { id -> onAction(DashboardAction.GotoUserChat(id!!)) },
                        onNewGroup = { id -> onAction(DashboardAction.GotoGroupChat(id!!)) },
                        onGoToChat = { id -> onAction(DashboardAction.GotoUserChat(id!!)) },
                    )
                }

                "WatchLater" -> {
                    Text("Watch Later", modifier = Modifier.padding(16.dp))
                }

                "Profile" -> {
                    CurrentProfileScreenRoot(
                        viewModel = CurrentProfileViewModel(state.user.id!!),
                        onFriendListNavigation = { id -> onAction(DashboardAction.OnFriendListNavigate(id)) },
                        onPostClickNavigation = { id -> onAction(DashboardAction.OnPostClick(id)) },
                        onProfileEditNavigation = { onAction(DashboardAction.OnEditProfileNavigate) },
                        onPostEditNavigation = { id -> onAction(DashboardAction.OnPostEditNavigate(id)) }
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewDashboardScreen() {
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            DashboardScreen(
                state = DashboardState(
                    selectedTab = "Profile"
                    //Home, Search, Chat, WatchLater, Profile
                ),
                onAction = {}
            )
        }
    }
}