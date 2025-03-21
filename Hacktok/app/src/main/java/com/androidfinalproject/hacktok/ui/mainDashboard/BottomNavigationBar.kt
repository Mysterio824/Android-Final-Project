package com.androidfinalproject.hacktok.ui.mainDashboard

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.filled.Search

@Composable
fun BottomNavigationBar(currentScreen: String, onItemSelected: (String) -> Unit) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        val items = listOf(
            BottomNavItem("Search", Icons.Filled.Search),
            BottomNavItem("Profile", Icons.Filled.Person),
            BottomNavItem("Chat", Icons.Filled.Chat),
            BottomNavItem("WatchLater", Icons.Filled.AccessTime)
        )

        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentScreen == item.label,
                onClick = { onItemSelected(item.label) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


data class BottomNavItem(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
