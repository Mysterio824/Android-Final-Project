package com.androidfinalproject.hacktok.ui.adminManage.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun AdminTabBar(
    currentScreen: String,
    onItemSelected: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        tonalElevation = 4.dp
    ) {
        NavigationBar {
            val items = listOf(
                BottomNavItem("Posts", Icons.Filled.Newspaper),
                BottomNavItem("Users", Icons.Filled.People),
                BottomNavItem("Comments", Icons.Filled.Comment),
                BottomNavItem("Report", Icons.Filled.Report),
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
}

data class BottomNavItem(val label: String, val icon: ImageVector)