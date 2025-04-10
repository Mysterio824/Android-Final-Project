package com.androidfinalproject.hacktok.ui.mainDashboard.component

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun BottomNavigationBar(
    currentScreen: String,
    onItemSelected: (String) -> Unit
) {
    NavigationBar {
        val items = listOf(
            BottomNavItem("Home", Icons.Filled.Home),
            BottomNavItem("Search", Icons.Filled.Search),
            BottomNavItem("Chat", Icons.Filled.ChatBubble),
            BottomNavItem("WatchLater", Icons.Filled.VideoLibrary),
            BottomNavItem("Profile", Icons.Filled.Person),
        )

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = ""
                    )
                },
                selected = currentScreen == item.label,
                onClick = { onItemSelected(item.label) },
                alwaysShowLabel = false
            )
        }
    }
}

data class BottomNavItem(val label: String, val icon: ImageVector)