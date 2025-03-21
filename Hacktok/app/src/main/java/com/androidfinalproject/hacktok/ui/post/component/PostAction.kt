package com.androidfinalproject.hacktok.ui.post.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.Post

@Composable
fun PostActions(
    post: Post,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        ActionButton(
            icon = Icons.Outlined.FavoriteBorder,
            text = "Like",
            onClick = onLikeClick,
            modifier = Modifier.weight(1f)
        )

        ActionButton(
            icon = Icons.Outlined.ModeComment,
            text = "Comment",
            onClick = onCommentClick,
            modifier = Modifier.weight(1f)
        )

        ActionButton(
            icon = Icons.Outlined.Share,
            text = "Share",
            onClick = onShareClick,
            modifier = Modifier.weight(1f)
        )
    }
}
