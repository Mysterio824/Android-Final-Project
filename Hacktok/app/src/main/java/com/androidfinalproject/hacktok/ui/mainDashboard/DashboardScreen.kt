package com.androidfinalproject.hacktok.ui.mainDashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.mainDashboard.home.HomeScreenRoot
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme
import androidx.activity.compose.BackHandler
import com.androidfinalproject.hacktok.ui.mainDashboard.component.TopNavigationBar
import com.androidfinalproject.hacktok.ui.mainDashboard.friendList.FriendListScreenRoot
import com.androidfinalproject.hacktok.ui.mainDashboard.friendList.FriendListViewModel
import com.androidfinalproject.hacktok.ui.mainDashboard.home.HomeScreenViewModel
import com.androidfinalproject.hacktok.ui.mainDashboard.watchLater.WatchLaterScreenRoot
import com.androidfinalproject.hacktok.ui.mainDashboard.watchLater.WatchLaterViewModel


@Composable
fun DashboardScreen(
    state: DashboardState,
    onAction: (DashboardAction) -> Unit
) {
    BackHandler {
        onAction(DashboardAction.OnNavigateBack)
    }
    Scaffold(
        topBar = {
            TopNavigationBar(
                currentTab = state.selectedTab,
                onSearchClick = { onAction(DashboardAction.OnSearchNavigate) },
                onMessageClick = { onAction(DashboardAction.OnMessageDashboardNavigate) },
                onUserClick = { onAction(DashboardAction.OnCurrentProfileNavigate) },
                onTabSelected = { onAction(DashboardAction.SelectTab(it)) }
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
                        viewModel = HomeScreenViewModel(state.user),
                        onUserClick = { onAction(DashboardAction.OnUserClick(it)) },
                        onPostClick = { onAction(DashboardAction.OnPostClick(it)) },
                        onStoryClick = { onAction(DashboardAction.OnStoryClick(it)) },
                        onNewPostNavigate = { onAction(DashboardAction.OnCreatePost) },
                        onNewStoryNavigate = { onAction(DashboardAction.OnCreateStory) }
                    )
                }

                "Friends" -> {
                    FriendListScreenRoot (
                        viewModel = FriendListViewModel(state.user.id!!),
                        onUserProfileView = { onAction(DashboardAction.OnUserClick(it)) },
                        onChatWithFriend = { onAction(DashboardAction.GotoUserChat(it)) },
                    )
                }

                "WatchLater" -> {
                    WatchLaterScreenRoot(
                        viewModel = WatchLaterViewModel(state.user.id!!),
                        onPostClickNavigation = { id -> onAction(DashboardAction.OnPostClick(id)) },
                        onUserProfileNavigate = { id -> onAction(DashboardAction.OnUserClick(id)) }
                    )
                }

                "Notifications" -> {
                    //TODO
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
                    selectedTab = "WatchLater"
                    //Home, Notifications, WatchLater, Friends
                ),
                onAction = {}
            )
        }
    }
}