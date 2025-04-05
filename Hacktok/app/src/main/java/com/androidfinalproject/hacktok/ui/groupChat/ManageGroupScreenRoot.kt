package com.androidfinalproject.hacktok.ui.groupChat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun ManageGroupScreenRoot(
    navController: NavController,
    viewModel: GroupChatViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    ManageGroupScreen(
        group = state.group,
        membersList = state.membersList,
        currentUserId = state.currentUser.id ?: "",
        isMuted = state.isGroupMuted,
        onToggleMute = { viewModel.onAction(GroupChatAction.ToggleMute) },
        onRenameGroup = { newName -> viewModel.onAction(GroupChatAction.RenameGroup(newName)) },
        onFindInChat = { viewModel.onAction(GroupChatAction.FindInChat) },
        onLeaveGroup = { viewModel.onAction(GroupChatAction.LeaveGroup) }
    )
}