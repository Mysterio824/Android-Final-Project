package com.androidfinalproject.hacktok.ui.mainDashboard;

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PostActionBar() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        IconButton(onClick = { /* Like Post */ }) {
            Icon(imageVector = Icons.Default.Favorite, contentDescription = "Like")
        }
        IconButton(onClick = { /* Comment Post */ }) {
            Icon(imageVector = Icons.Default.Comment, contentDescription = "Comment")
        }
        IconButton(onClick = { /* Share Post */ }) {
            Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
        }
    }
}
