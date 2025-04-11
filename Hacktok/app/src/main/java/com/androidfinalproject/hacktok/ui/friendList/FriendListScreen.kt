package com.androidfinalproject.hacktok.ui.friendList

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.RelationInfo
import com.androidfinalproject.hacktok.model.RelationshipStatus
import com.androidfinalproject.hacktok.ui.friendList.component.*
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendListScreen(
    state: FriendListState,
    onAction: (FriendListAction) -> Unit
) {
    var selectedFriendId by remember { mutableStateOf<String?>(null) }

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Friends",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        FriendListSearchBar(
            searchQuery = state.searchQuery,
            onSearchQueryChange = {
                onAction(FriendListAction.SearchQueryChanged(it))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = state.error,
                    color = Color.Red
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.filteredUsers) { user ->
                    val info = state.relations[user.id] ?: RelationInfo("")

                    if (info.status == RelationshipStatus.BLOCKED ||
                        info.status == RelationshipStatus.BLOCKING
                    ) {

                        BlockedListItem(
                            relation = info,
                            onUnblockUser = { onAction(FriendListAction.OnUnBlockFriend(user.id!!)) }
                        )

                    } else {
                        FriendListItem(
                            user = user,
                            relation = info,
                            onSendFriendRequest = {
                                onAction(FriendListAction.SendFriendRequest(user.id!!, it))
                            },
                            onAcceptRequest = {
                                onAction(FriendListAction.OnAcceptFriendRequest(user.id!!, it))
                            },
                            onOptionsClick = {
                                selectedFriendId = user.id
                            },
                            onUserClick = {
                                onAction(FriendListAction.UserClicked(user.id!!))
                            }
                        )
                    }
                }
            }

            if (selectedFriendId != null) {
                ModalBottomSheet(
                    onDismissRequest = { selectedFriendId = null },
                    sheetState = bottomSheetState
                ) {
                    FriendOptionsContent (
                        onUnFriend = {},
                        onChat = { onAction(FriendListAction.ChatWithFriend(selectedFriendId!!)) },
                        onBlock = { onAction(FriendListAction.OnBlockFriend(selectedFriendId!!)) },
                        onDismiss = { selectedFriendId = null }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FriendListPreview(){
    MainAppTheme{
        Box{
            val mockUsers = MockData.mockUsers
            FriendListScreen(
                state = FriendListState(
                    users = mockUsers,
                    filteredUsers = mockUsers,
                    isLoading = false
                ),
                onAction = {}
            )
        }
    }
}