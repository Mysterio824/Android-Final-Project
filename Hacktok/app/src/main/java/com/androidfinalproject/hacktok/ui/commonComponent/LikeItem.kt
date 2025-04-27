package com.androidfinalproject.hacktok.ui.commonComponent

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


@Composable
fun LikeItem(
    username: String,
    imageUrl: String?,
    reactionType: String,
    onClick: () -> Unit
) {
    androidx.compose.material3.ListItem(
        headlineContent = {
            Text(
                text = username,
                fontWeight = FontWeight.Medium
            )
        },
        leadingContent = {
            Box {
                ProfileImage(
                    imageUrl = imageUrl,
                    size = 40.dp,
                    onClick = onClick
                )

                // Reaction icon in bottom right corner
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(16.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Image(
                        imageVector = getReactionIcon(reactionType),
                        contentDescription = "Reaction",
                        modifier = Modifier
                            .size(14.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        },
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}