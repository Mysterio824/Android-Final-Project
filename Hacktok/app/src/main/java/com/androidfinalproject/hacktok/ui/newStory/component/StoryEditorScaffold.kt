package com.androidfinalproject.hacktok.ui.newStory.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.newPost.PRIVACY

@Composable
fun StoryEditorScaffold(
    privacy: PRIVACY,
    onPrivacyChange: (PRIVACY) -> Unit,
    onClose: () -> Unit,
    onSend: () -> Unit,
    background: @Composable BoxScope.() -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Optional background layer (gradient/image)
        background()

        // Close button
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(36.dp)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White
            )
        }

        // Send button
        IconButton(
            onClick = onSend,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(36.dp)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Post",
                tint = Color.White
            )
        }

        // Privacy selector
        PrivacyOptionButton(
            selected = privacy,
            onSelected = onPrivacyChange,
            modifier = Modifier.align(Alignment.BottomStart)
        )

        // Screen-specific content
        content()
    }
}