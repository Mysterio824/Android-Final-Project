package com.androidfinalproject.hacktok.ui.chatDetail

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.ui.chatDetail.components.GroupHeader
import com.androidfinalproject.hacktok.ui.chatDetail.components.MembersList
import com.androidfinalproject.hacktok.ui.chatDetail.components.RenameGroupDialog
import com.androidfinalproject.hacktok.ui.chatDetail.components.UserHeader
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.androidfinalproject.hacktok.model.Group
import com.androidfinalproject.hacktok.model.User
import java.util.Date

@Composable
fun ChatDetailScreen(
    state: ChatDetailState,
    onAction: (ChatDetailAction) -> Unit
) {
    var isRenameDialogVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.isGroup) {
            GroupHeader(
                group = state.group,
                currentUser = state.currentUser,
                membersList = state.membersList,
                onRenameClick = {
                    isRenameDialogVisible = true
                }
            )
        } else {
            state.otherUser?.let { user ->
                UserHeader(
                    user = user,
                    onViewProfileClick = { onAction(ChatDetailAction.NavigateToUserProfile(user.id)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons - Chung cho cả hai loại chat
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onAction(ChatDetailAction.ToggleMute) }) {
                Icon(
                    imageVector = if (state.isUserMuted) Icons.Default.NotificationsOff else Icons.Default.Notifications,
                    contentDescription = "Toggle Mute",
                    tint = if (state.isUserMuted) Color.Red else Color.Gray
                )
            }

            // Nút nhóm (chỉ hiển thị khi là chat cá nhân)
            if (!state.isGroup) {
                IconButton(onClick = { onAction(ChatDetailAction.CreateGroup) }) {
                    Icon(
                        imageVector = Icons.Default.GroupAdd,
                        contentDescription = "Create Group",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            IconButton(onClick = { onAction(ChatDetailAction.FindInChat) }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Find in Chat",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Phần nội dung khác nhau dựa vào loại chat
        if (state.isGroup) {
            // Tiêu đề danh sách thành viên
            Text(
                text = "Thành viên",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Danh sách thành viên
            MembersList(
                membersList = state.membersList,
                group = state.group,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nút rời nhóm
            Button(
                onClick = { onAction(ChatDetailAction.LeaveGroup) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
            ) {
                Text("Rời nhóm", color = Color.White)
            }
        } else {
            // Đối với chat cá nhân
            state.otherUser?.let { user ->
                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { onAction(ChatDetailAction.BlockUser) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                ) {
                    Text("Chặn người dùng", color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { onAction(ChatDetailAction.DeleteChat) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
                ) {
                    Text("Xóa cuộc trò chuyện", color = Color.White)
                }
            }
        }
    }

    if (isRenameDialogVisible) {
        RenameGroupDialog(
            initialGroupName = state.group.groupName,
            onDismiss = { isRenameDialogVisible = false },
            onConfirm = { newName ->
                onAction(ChatDetailAction.RenameGroup(newName))
                isRenameDialogVisible = false
            }
        )
    }
}


@Preview(showBackground = true, widthDp = 360, heightDp = 740, name = "Group Chat Preview")
@Composable
fun GroupChatPreview() {
    // Tạo state giả lập cho group chat
    val groupState = ChatDetailState(
        currentUser = User(id = "user1", username = "User One", email = "user1@example.com"),
        group = Group(
            id = "group1",
            groupName = "Nhóm bạn thân",
            description = "Nhóm chat của những người bạn thân",
            creatorId = "user1",
            members = listOf("user1", "user2", "user3", "user4"),
            admins = listOf("user1"),
            isPublic = true,
            createdAt = Date(),
            coverImage = null
        ),
        membersList = listOf(
            User(id = "user1", username = "Nguyễn Văn A", email = "nguyenvana@example.com"),
            User(id = "user2", username = "Trần Thị B", email = "tranthib@example.com"),
            User(id = "user3", username = "Phạm Văn C", email = "phamvanc@example.com"),
            User(id = "user4", username = "Lê Thị D", email = "lethid@example.com")
        ),
        isGroup = true,
        isUserMuted = false
    )

    ChatDetailScreen(
        state = groupState,
        onAction = {}
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 740, name = "Individual Chat Preview")
@Composable
fun IndividualChatPreview() {
    val individualState = ChatDetailState(
        currentUser = User(id = "user1", username = "User One", email = "user1@example.com"),
        otherUser = User(
            id = "user2",
            username = "Trần Thị B",
            email = "tranthib@example.com"
        ),
        isGroup = false,
        isUserMuted = true  // Set thành true để thấy icon mute
    )

    ChatDetailScreen(
        state = individualState,
        onAction = {}
    )
}
