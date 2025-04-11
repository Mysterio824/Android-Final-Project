package com.androidfinalproject.hacktok.ui.mainDashboard.friendSuggestion.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.User

@Composable
fun FriendRequestsSection(
    pendingRequests: List<User>,
    onAccept: (String) -> Unit,
    onDecline: (String) -> Unit,
    onUserClick: (String) -> Unit
) {
    if (pendingRequests.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(
                text = "Friend Requests (${pendingRequests.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            pendingRequests.forEach { user ->
                FriendRequestItem(
                    user = user,
                    onAccept = { onAccept(user.id ?: "") },
                    onDecline = { onDecline(user.id ?: "") },
                    onUserClick = { onUserClick(user.id ?: "") }
                )
                HorizontalDivider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}