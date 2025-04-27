package com.androidfinalproject.hacktok.ui.commonComponent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.model.FullReaction

@Composable
fun LikeListContent(
    listEmotions: List<FullReaction>,
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
        Text(
            text = "${listEmotions.size} Likes",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        HorizontalDivider()

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(listEmotions, key = { it.user.id!! }) { item ->
                OptionItem(
                    title = item.user.username ?: "",
                    imageUrl = item.user.profileImage,
                    onClick = withDismiss { onUserClick(item.user.id!!) }
                )
            }
        }
    }
}