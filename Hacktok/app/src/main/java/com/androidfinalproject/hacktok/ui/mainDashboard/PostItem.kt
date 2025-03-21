package com.androidfinalproject.hacktok.ui.mainDashboard;

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.Post

@Composable
fun PostItem(post: Post) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = post.user.username, style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = post.content)
            Spacer(modifier = Modifier.height(8.dp))
            PostActionBar()
        }
    }
}

