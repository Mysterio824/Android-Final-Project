package com.androidfinalproject.hacktok.ui.commonComponent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ShareOptionsContent(
    onDismiss: () -> Unit,
    onShareToFeed: () -> Unit = {},
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
            text = "Share",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(16.dp)
        )

        OptionItem(
            title = "Share to News Feed",
            onClick = {
                onShareToFeed()
                onDismiss()
            }
        )

        OptionItem(
            title = "Share to Your Story",
            onClick = onDismiss
        )

        OptionItem(
            title = "Send in Messenger",
            onClick = onDismiss
        )

        OptionItem(
            title = "Share to a Group",
            onClick = onDismiss
        )

        OptionItem(
            title = "Copy Link",
            onClick = onDismiss
        )
    }
}