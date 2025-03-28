package com.androidfinalproject.hacktok.ui.adminManage.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.model.Comment
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User

@Composable
fun StatisticsTab(
    users: List<User>,
    posts: List<Post>,
    comments: List<Comment>
) {
    val activeUsers = users.count { it.isActive }
    val totalUsers = users.size
    val newUsers = users.count { user ->
        val oneMonthAgo = java.util.Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000)
        user.createdAt.after(oneMonthAgo)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Platform Statistics",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Total Users: $totalUsers")
                Text(text = "Active Users: $activeUsers")
                Text(text = "New Users (Last 30 Days): $newUsers")
                Text(text = "Total Posts: ${posts.size}")
                Text(text = "Total Comments: ${comments.size}")
            }
        }
    }
}