package com.androidfinalproject.hacktok.ui.commonComponent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.User

@Composable
fun LikeListContent(
    users: List<User>,
    onUserClick: (String) -> Unit,
    onDismiss: () -> Unit
){
    fun withDismiss(action: () -> Unit): () -> Unit = {
        action()
        onDismiss()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(users, key = { it.id!! }) { user ->
                OptionItem(
                    title = user.username ?: "",
                    imageUrl = user.profileImage,
                    onClick = withDismiss { onUserClick(user.id!!) }
                )
            }
        }
    }
}