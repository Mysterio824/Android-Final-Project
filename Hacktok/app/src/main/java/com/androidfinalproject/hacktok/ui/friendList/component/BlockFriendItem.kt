package com.androidfinalproject.hacktok.ui.friendList.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.model.RelationInfo
import com.androidfinalproject.hacktok.model.enums.RelationshipStatus
import com.androidfinalproject.hacktok.ui.commonComponent.ProfileImage


@Composable
fun BlockedListItem(
    relation: RelationInfo,
    onUnblockUser: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 72.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // generic avatar
        ProfileImage(
            imageUrl = "",
            size = 50.dp
        )

        Spacer(Modifier.width(16.dp))

        Column(Modifier.weight(1f)) {
            Text("User", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("", fontSize = 14.sp)
        }

        when (relation.status) {
            RelationshipStatus.BLOCKED -> {
                Text("Blocked you", fontSize = 20.sp, color = Color.Red)
            }
            RelationshipStatus.BLOCKING -> {
                Button(onClick = onUnblockUser) { Text("Unblock") }
            }
            else -> { }
        }
    }

    HorizontalDivider(
        color = Color.LightGray,
        thickness = 1.dp,
        modifier = Modifier.padding(top = 8.dp)
    )
}