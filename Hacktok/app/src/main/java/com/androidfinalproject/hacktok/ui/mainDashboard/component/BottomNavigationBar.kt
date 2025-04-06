package com.androidfinalproject.hacktok.ui.mainDashboard.component

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun BottomNavigationBar(currentScreen: String, onItemSelected: (String) -> Unit) {
    NavigationBar {
        val items = listOf(
            BottomNavItem("Search", Icons.Filled.Search),
            BottomNavItem("Profile", Icons.Filled.Person),
            BottomNavItem("Chat", Icons.Filled.Chat),
            BottomNavItem("Watchlater", Icons.Filled.AccessTime)
        )

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = currentScreen == item.label,
                onClick = { onItemSelected(item.label) }
            )
        }
    }
}

data class BottomNavItem(val label: String, val icon: ImageVector)