package com.androidfinalproject.hacktok.ui.post.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


@Composable
fun CommentsSectionToggle(
    commentCount: Int,
    showComments: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        TextButton(
            onClick = onToggle,
            contentPadding = PaddingValues(horizontal = 25.dp)
        ) {
            Text(
                text = if (showComments) "Hide comments" else "View all $commentCount comments",
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}