package com.androidfinalproject.hacktok.ui.chatDetail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.model.Group
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.enums.RelationshipStatus
import com.androidfinalproject.hacktok.ui.chatDetail.components.GroupHeader
import com.androidfinalproject.hacktok.ui.chatDetail.components.MembersList
import com.androidfinalproject.hacktok.ui.chatDetail.components.RenameGroupDialog
import com.androidfinalproject.hacktok.ui.commonComponent.ProfileImage
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    state: ChatDetailState,
    onAction: (ChatDetailAction) -> Unit
) {
    var isRenameDialogVisible by remember { mutableStateOf(false) }
    val isBlock = state.relation.status == RelationshipStatus.BLOCKED ||
            state.relation.status == RelationshipStatus.BLOCKING
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.isGroup) "Thông tin nhóm" else "Thông tin người dùng",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onAction(ChatDetailAction.NavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
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
                    ProfileImage(
                        imageUrl = if(isBlock) "" else user.profileImage,
                        size = 100.dp,
                        onClick = { onAction(ChatDetailAction.NavigateToUserProfile) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons - Centered below header
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FilledTonalIconButton(
                        onClick = { onAction(ChatDetailAction.ToggleMute) },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (state.isUserMuted) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = if (state.isUserMuted) Icons.Default.NotificationsOff else Icons.Default.Notifications,
                            contentDescription = "Toggle Mute"
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (state.isUserMuted) "Đã tắt âm" else "Thông báo",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Group button (only shown for individual chat)
                if (!state.isGroup) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        FilledTonalIconButton(
                            onClick = { onAction(ChatDetailAction.CreateGroup) },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.GroupAdd,
                                contentDescription = "Create Group"
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tạo nhóm",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(24.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))

            // Different content based on chat type
            if (state.isGroup) {
                // Members list header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Thành viên",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${state.membersList.size} người",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Members list
                MembersList(
                    membersList = state.membersList,
                    group = state.group,
                    onMemberAction = onAction,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Leave group button
                Button(
                    onClick = { onAction(ChatDetailAction.LeaveGroup) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        "Rời nhóm",
                        color = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            } else {
                // For individual chat
                state.otherUser?.let { _ ->
                    Spacer(modifier = Modifier.weight(1f))
                    if(state.relation.status != RelationshipStatus.BLOCKED){
                        val isBlocking = state.relation.status == RelationshipStatus.BLOCKING
                        Button(
                            onClick = {
                                if(isBlocking)
                                    onAction(ChatDetailAction.UnBlockUser)
                                else
                                    onAction(ChatDetailAction.BlockUser)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            ),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(
                                text = if(!isBlocking) "Chặn người dùng" else "Bỏ chặn người dùng",
                                color = MaterialTheme.colorScheme.onError,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }


                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { onAction(ChatDetailAction.DeleteChat) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            "Xóa cuộc trò chuyện",
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
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
    // Sample data for preview
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
        isUserMuted = true  // Set to true to show mute icon
    )

    ChatDetailScreen(
        state = individualState,
        onAction = {}
    )
}