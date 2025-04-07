package com.androidfinalproject.hacktok.ui.mainDashboard.messageDashboard


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
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.ui.mainDashboard.messageDashboard.component.UserSelection
import com.androidfinalproject.hacktok.ui.mainDashboard.messageDashboard.component.ActionButton
import com.androidfinalproject.hacktok.ui.mainDashboard.messageDashboard.component.SearchBar
import com.androidfinalproject.hacktok.ui.mainDashboard.messageDashboard.component.ChatList
import com.androidfinalproject.hacktok.ui.mainDashboard.watchLater.WatchLaterScreen
import com.androidfinalproject.hacktok.ui.mainDashboard.watchLater.WatchLaterState
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@Composable
fun MessageDashboardScreen (
    state: MessageDashboardState,
    onAction: (MessageDashboardAction) -> Unit,
) {
    Column (
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
            ActionButton(icon= Icons.Default.Add, contentDescription = "Create", menuItems = listOf(
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
        )

        Spacer(modifier = Modifier.height(8.dp))

        UserSelection(userLists = state.userList)

        Spacer(modifier = Modifier.height(8.dp))

        ChatList(
            friendList = emptyList(),
            menuItems = listOf(
            "Delete chat" to {},
            "Mute" to {},
            "Create group with" to {},
            "Block" to {},
            ),
            onAction = onAction
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MessageDashboardScreenPreview() {
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            MessageDashboardScreen(
                state = MessageDashboardState(
                    userList = MockData.mockUsers
                ),
                onAction = {}
            )
        }
    }
}