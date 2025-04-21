package com.androidfinalproject.hacktok.ui.messageDashboard.component

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.model.Chat
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.enums.RelationshipStatus
import com.androidfinalproject.hacktok.ui.commonComponent.ProfileImage
import com.androidfinalproject.hacktok.ui.messageDashboard.MessageDashboardAction
import com.mongodb.Block
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

@Composable
fun ChatRow(
    user: User,
    onClick: () -> Unit,
    chat: Chat,
    status: RelationshipStatus,
    onOptionClick: () -> Unit,
) {
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onClick() },
                        onLongPress = {
                            if(status != RelationshipStatus.BLOCKED)
                                onOptionClick()
                        }
                    )
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ProfileImage(
                imageUrl = if(status == RelationshipStatus.BLOCKING || status == RelationshipStatus.BLOCKED) "" else user.profileImage,
                size = 60.dp
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.username!!, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = chat.lastMessage,
                        fontSize = 16.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    FormattedMessageTime(chat.lastMessageAt)
                }
            }
        }
    }
}

@Composable
private fun FormattedMessageTime(date: Date) {
    val formatted = remember(date) {
        val zoneId = ZoneId.systemDefault()
        val messageDateTime = date.toInstant().atZone(zoneId).toLocalDateTime()
        val now = LocalDateTime.now(zoneId)

        val formatterToday = DateTimeFormatter.ofPattern("HH:mm")
        val formatterYesterday = DateTimeFormatter.ofPattern("EEE") // e.g., "Fri"

        when {
            messageDateTime.toLocalDate() == now.toLocalDate() -> {
                messageDateTime.format(formatterToday) // Today → show time
            }
            messageDateTime.toLocalDate() == now.minusDays(1).toLocalDate() -> {
                messageDateTime.format(formatterYesterday) // Yesterday → show "Fri"
            }
            else -> {
                messageDateTime.format(DateTimeFormatter.ofPattern("dd MMM")) // fallback: 21 Apr
            }
        }
    }

    Text(
        text = formatted,
        fontSize = 14.sp,
        color = Color.Gray
    )
}
