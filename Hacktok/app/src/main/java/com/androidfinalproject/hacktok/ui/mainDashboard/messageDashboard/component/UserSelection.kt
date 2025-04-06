package com.androidfinalproject.hacktok.ui.mainDashboard.messageDashboard.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.model.User

@Composable
fun UserSelection(
    userLists: List<User>,
) {
    LazyRow(modifier = Modifier.fillMaxWidth()) { // Ensure full width
        items(userLists) { user ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)
            ) {
                ProfileImage(imageSize = 64.dp, modifier = Modifier.fillMaxWidth(), contentDescription = "Story Picture", isActive = true)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = user.fullName ?: "Unknown", fontSize = 12.sp, color = Color.Black)
            }
        }
    }
}