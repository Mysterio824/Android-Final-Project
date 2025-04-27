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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.R


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
                text = if (showComments) stringResource(R.string.hide_cmt) else stringResource(R.string.view_cmt, commentCount),
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}