package com.androidfinalproject.hacktok.ui.friendList.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.commonComponent.OptionItem

@Composable
fun FriendOptionsContent(
    onUnFriend: () -> Unit,
    onChat: () -> Unit,
    onBlock: () -> Unit,
    onDismiss: () -> Unit,
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
        OptionItem(
            title = "Unfriend",
            icon = Icons.Default.PersonRemove,
            onClick = withDismiss(onUnFriend)
        )

        OptionItem(
            title = "Chat",
            icon = Icons.AutoMirrored.Filled.Chat,
            onClick = withDismiss(onChat)
        )

        OptionItem(
            title = "Block",
            icon = Icons.Default.Block,
            onClick = withDismiss(onBlock)
        )
    }
}
