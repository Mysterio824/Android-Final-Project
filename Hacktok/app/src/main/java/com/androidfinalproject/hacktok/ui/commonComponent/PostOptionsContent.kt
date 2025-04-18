package com.androidfinalproject.hacktok.ui.commonComponent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun PostOptionsContent(
    isPostOwner: Boolean,
    onDismiss: () -> Unit,
    onReport: () -> Unit
) {
    fun withDismiss(action: () -> Unit): () -> Unit = {
        action()
        onDismiss()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = "Post Options",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(16.dp)
        )

        // Common options for all users
        OptionItem(
            title = "Hide Post",
            description = "See fewer posts like this",
            onClick = onDismiss
        )

        OptionItem(
            title = "Report Post",
            description = "This post concerns me",
            onClick = withDismiss(onReport)
        )

        OptionItem(
            title = "Save Post",
            description = "Add to your saved items",
            onClick = onDismiss
        )

        // Options only for post owner
        if (isPostOwner) {
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            OptionItem(
                title = "Edit Post",
                icon = Icons.Default.Edit,
                onClick = onDismiss
            )

            OptionItem(
                title = "Delete Post",
                icon = Icons.Default.Delete,
                onClick = onDismiss
            )

            OptionItem(
                title = "Change Privacy",
                icon = Icons.Default.Public,
                onClick = onDismiss
            )
        }
    }
}