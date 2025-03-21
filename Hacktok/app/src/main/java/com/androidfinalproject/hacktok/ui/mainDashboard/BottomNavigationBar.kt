package com.androidfinalproject.hacktok.ui.mainDashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavigationBar(
    currentScreen: String,
    onItemSelected: (String) -> Unit
) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary
    ) {
        val items = listOf(
            BottomNavItem("Search", Icons.Filled.Search),
            BottomNavItem("Chat", Icons.Filled.Chat),
            BottomNavItem("WatchLater", Icons.Filled.AccessTime),
            BottomNavItem("Profile", Icons.Filled.Person)

        )

        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentScreen == item.label,
                onClick = { onItemSelected(item.label) }
            )
        }
    }
}

data class BottomNavItem(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
