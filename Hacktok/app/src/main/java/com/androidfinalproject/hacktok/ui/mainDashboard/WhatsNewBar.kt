package com.androidfinalproject.hacktok.ui.mainDashboard;

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WhatsNewBar(onSearch: (String) -> Unit) {
    var query by remember { mutableStateOf("") }

    TextField(
        value = query,
        onValueChange = { query = it },
        placeholder = { Text("What's new...") },
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    )
}
