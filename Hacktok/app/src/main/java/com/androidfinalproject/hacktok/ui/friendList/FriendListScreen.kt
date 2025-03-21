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
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.ui.friendList.component.*

@Composable
fun FriendListScreen(
    state: FriendListState,
    onAction: (FriendListAction) -> Unit
) {
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
                    val isFriend = state.friendIds.contains(user.id)
                    FriendListItem(
                        user = user,
                        isFriend = isFriend,
                        onAddFriend = {
                            onAction(FriendListAction.AddFriend(user))
                        },
                        onChatWithFriend = {
                            onAction(FriendListAction.ChatWithFriend(user))
                        },
                        onUserClick = {
                            onAction(FriendListAction.UserClicked(user))
                        }
                    )
                    Divider(
                        color = Color.LightGray,
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}