package com.androidfinalproject.hacktok.ui.mainDashboard.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigationBar(
    currentTab: String,
    onSearchClick: () -> Unit,
    onMessageClick: () -> Unit,
    onUserClick: () -> Unit,
    onLogOut: () -> Unit,
    onTabSelected: (String) -> Unit
) {
    val facebookBlue = Color(0xFF1877F2)
    val tabs = listOf(
        "Home" to Icons.Default.Home,
        "Friends" to Icons.Outlined.Group,
        "WatchLater" to Icons.Default.VideoLibrary,
        "Notifications" to Icons.Outlined.Notifications
    )

    Column {
        TopAppBar(
            title = {
                Text(
                    "HackTok",
                    color = facebookBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                )
            },
            actions = {
                IconButton(
                    onClick = { onSearchClick() },
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(40.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Black)
                }

                IconButton(
                    onClick = { onMessageClick() },
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(40.dp)
                ) {
                    Icon(Icons.AutoMirrored.Outlined.Message, contentDescription = "Messenger", tint = Color.Black)
                }

                UserDropdownMenu(
                    onProfileClick = onUserClick,
                    onLogoutClick = onLogOut
                )

            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )

        TabRow(
            selectedTabIndex = tabs.indexOfFirst { it.first == currentTab }.takeIf { it >= 0 } ?: 0,
            containerColor = Color.White,
            contentColor = facebookBlue,
            indicator = {},
            divider = {},
            modifier = Modifier.height(40.dp)
        ) {
            tabs.forEach { (tabName, tabIcon) ->
                Tab(
                    selected = currentTab == tabName,
                    onClick = { onTabSelected(tabName) },
                    text = null,
                    icon = {
                        Icon(
                            imageVector = tabIcon,
                            contentDescription = tabName,
                            tint = if (currentTab == tabName) facebookBlue else Color.Gray,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                )
            }
        }

        HorizontalDivider(thickness = 1.dp, color = Color(0xFFDDDDDD))
    }
}