package com.androidfinalproject.hacktok.ui.messageDashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.ui.messageDashboard.component.*
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageDashboardScreen (
    state: MessageDashboardState,
    onAction: (MessageDashboardAction) -> Unit,
) {

    var selectChatItem by remember { mutableStateOf<ChatItem?>(null) }

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    val isRefreshing = state.isLoading
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = {
            onAction(MessageDashboardAction.Refresh)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            // Header which contains the chats label and button to send messages to friends
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chats",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                ActionButton(icon = Icons.Default.Add,
                    contentDescription = "Create",
                    menuItems = listOf(
                        "New Chat" to {
                            onAction(MessageDashboardAction.NewChat)
                        },
                        "New Group" to {
                            onAction(MessageDashboardAction.NewGroup)
                        }
                    ))
            }

            Spacer(modifier = Modifier.height(8.dp))

            SearchBar(
                modifier = Modifier.fillMaxWidth(),
                searchQuery = state.searchQuery,
                onQueryChange = { onAction(MessageDashboardAction.SearchQueryChanged(it)) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.height(8.dp))

            ChatList(
                chatList = state.filterChatList,
                onAction = onAction,
                onOptionClick = { selectChatItem = it }
            )
        }

        if (selectChatItem != null) {
            ModalBottomSheet(
                onDismissRequest = { selectChatItem = null },
                sheetState = bottomSheetState
            ) {
                ChatOptionsContent(
                    onDismiss = { selectChatItem = null },
                    onDelete = { onAction(MessageDashboardAction.DeleteChat(selectChatItem!!.chat.id!!)) },
                    onBlock = { onAction(MessageDashboardAction.BlockChat(selectChatItem!!.user.id!!)) },
                    onUnblock = { onAction(MessageDashboardAction.UnBlockChat(selectChatItem!!.user.id!!)) },
                    onMute = { onAction(MessageDashboardAction.SetMute(selectChatItem!!.chat.id!!)) },
                    status = selectChatItem!!.relationInfo.status
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MessageDashboardScreenPreview() {
    MainAppTheme {
        MessageDashboardScreen(
            state = MessageDashboardState(
            ),
            onAction = {}
        )
    }
}