package com.androidfinalproject.hacktok.ui.post.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.Post
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PostHeader(
    post: Post,
    onUserClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // User avatar
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable { onUserClick() }
        ) {
            Text(
                text = MockData.mockUsers.first().username.first().toString(),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // User info
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable { onUserClick() }
        ) {
            Text(
                text = MockData.mockUsers.first().username,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = formatDate(post.createdAt),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }

        // More options
        IconButton(onClick = { /* TODO: Open more options */ }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options"
            )
        }
    }
}

fun formatDate(date: Date): String {
    val now = Calendar.getInstance()
    val postTime = Calendar.getInstance()
    postTime.time = date

    return when {
        now.get(Calendar.YEAR) != postTime.get(Calendar.YEAR) -> {
            SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)
        }
        now.get(Calendar.DAY_OF_YEAR) != postTime.get(Calendar.DAY_OF_YEAR) -> {
            SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
        }
        else -> {
            SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
        }
    }
}