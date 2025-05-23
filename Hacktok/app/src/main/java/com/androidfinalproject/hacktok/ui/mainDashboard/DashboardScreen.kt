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
import androidx.compose.ui.Alignment
import com.androidfinalproject.hacktok.ui.mainDashboard.component.TopNavigationBar
import com.androidfinalproject.hacktok.ui.mainDashboard.friendSuggestion.FriendSuggestionScreenRoot
import com.androidfinalproject.hacktok.ui.mainDashboard.notifcation.NotificationScreenRoot
import com.androidfinalproject.hacktok.ui.mainDashboard.settings.SettingsScreenRoot
import com.androidfinalproject.hacktok.ui.mainDashboard.watchLater.WatchLaterScreenRoot

//import com.androidfinalproject.hacktok.ui.mainDashboard.watchLater.WatchLaterScreenRoot


@Composable
fun DashboardScreen(
    state: DashboardState,
    onAction: (DashboardAction) -> Unit
) {
    BackHandler {
        onAction(DashboardAction.OnNavigateBack)
    }
    if(state.isLoading){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(
            topBar = {
                TopNavigationBar(
                    imageUrl = state.currentUser?.profileImage ?: "",
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
                            onUserClick = { onAction(DashboardAction.OnUserClick(it)) },
                            onPostClick = { onAction(DashboardAction.OnPostClick(it, null)) },
                            onPostEditClick = { onAction(DashboardAction.OnPostEditNavigate(it)) },
                            onStoryClick = { onAction(DashboardAction.OnStoryClick(it)) },
                            onNewPostNavigate = { onAction(DashboardAction.OnCreatePost) },
                            onCreateStoryNavigate = { onAction(DashboardAction.OnCreateStory) },
                            onImageClickNavigate = { onAction(DashboardAction.OnImageClick(it)) }
                        )
                    }

                    "Friends" -> {
                        FriendSuggestionScreenRoot (
                            onUserNavigate = { onAction(DashboardAction.OnUserClick(it)) },
                            onFriendListNavigate = { onAction(DashboardAction.OnFriendListNavigate(it)) },
                        )
                    }

                    "WatchLater" -> {
                        WatchLaterScreenRoot(
                            onPostClickNavigation = { onAction(DashboardAction.OnPostClick(it, null)) },
                            onUserProfileNavigate = { onAction(DashboardAction.OnUserClick(it)) }
                        )
                    }

                    "Notifications" -> {
                        NotificationScreenRoot(
                            onPostClick = { postId, commentId -> onAction(DashboardAction.OnPostClick(postId, commentId)) },
                            onUserClick = { onAction(DashboardAction.OnUserClick(it)) },
                        )
                    }

                    "Settings" -> {
                        SettingsScreenRoot (
                            onEditProfileNavigate = { onAction(DashboardAction.OnUserEdit) },
                            onAuthNavigate = { onAction(DashboardAction.OnAuthNavigate) },
                            onChangePasswordNavigate = { onAction(DashboardAction.OnChangePass) },
                            onCurrentProfileNavigate = { onAction(DashboardAction.OnCurrentProfileNavigate) },
                            onSecretCrushNavigate = { onAction(DashboardAction.OnSecretCrushNavigate) }

                        )
                    }
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
                    //Home, Notifications, WatchLater, Friends, Settings
                ),
                onAction = {}
            )
        }
    }
}