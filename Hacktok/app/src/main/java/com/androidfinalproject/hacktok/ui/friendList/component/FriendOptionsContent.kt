package com.androidfinalproject.hacktok.ui.friendList.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        Text(
            text = "Post Options",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(16.dp)
        )

        OptionItem(
            title = "Unfriend",
            description = "Unfriend with this user",
            onClick = withDismiss(onUnFriend)
        )

        OptionItem(
            title = "Chat",
            description = "Chat with this user",
            onClick = withDismiss(onChat)
        )

        OptionItem(
            title = "Block",
            description = "Block everything about this user",
            onClick = withDismiss(onBlock)
        )
    }
}
