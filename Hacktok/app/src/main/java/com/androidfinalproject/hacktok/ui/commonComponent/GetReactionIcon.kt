package com.androidfinalproject.hacktok.ui.commonComponent

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun getReactionIcon(reactionType: String): ImageVector {
    return when (reactionType) {
        "👍" -> Icons.Default.ThumbUp
        "❤️" -> Icons.Default.Favorite
        "😆" -> Icons.Default.EmojiEmotions
        "😮" -> Icons.Default.SentimentSatisfied
        "😢" -> Icons.Default.SentimentDissatisfied
        "😠" -> Icons.Default.SentimentVeryDissatisfied
        else -> Icons.Default.ThumbUp
    }
}