package com.androidfinalproject.hacktok.ui.mainDashboard.component;

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


@Composable
fun DashboardTopBar(onClick: () -> Unit) {
    TopAppBar(
        title = { Text(
            text = "Háck Tók",
            color = Color.White, // ✅ Chữ trắng
            fontWeight = FontWeight.Bold, // ✅ Đậm hơn
            fontSize = 22.sp // ✅ To hơn
        ) },
        backgroundColor = Color.Black,
        actions = {
            IconButton(onClick = onClick) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onSurface)
            }
        }
    )
}
