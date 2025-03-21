package com.androidfinalproject.hacktok.ui.mainDashboard;

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.androidfinalproject.hacktok.R

@Composable
fun DashboardTopBar(onSearchClick: () -> Unit) {
    TopAppBar(
        title = { Text("Hacktok") },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(imageVector = ImageVector.vectorResource(R.drawable.ic_launcher_background), contentDescription = "Search")
            }
        }
    )
}
