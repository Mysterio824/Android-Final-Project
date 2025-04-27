package com.androidfinalproject.hacktok.ui.mainDashboard.friendSuggestion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.RelationInfo
import com.androidfinalproject.hacktok.model.enums.RelationshipStatus
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.mainDashboard.friendSuggestion.component.*
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun FriendSuggestionScreen(
    state: FriendSuggestionState,
    onAction: (FriendSuggestionAction) -> Unit
) {

    val isRefreshing = state.isLoading

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = {
            onAction(FriendSuggestionAction.Refresh)
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header section
            item {
                Header(onFriendListClick = { onAction(FriendSuggestionAction.OnFriendListNavigate) })
            }

            item {
                FriendRequestsSection(
                    pendingRequests = filterPendingRequests(state.users, state.relations),
                    onAccept = { userId ->
                        onAction(
                            FriendSuggestionAction.HandleRequest(
                                userId,
                                true
                            )
                        )
                    },
                    onDecline = { userId ->
                        onAction(
                            FriendSuggestionAction.HandleRequest(
                                userId,
                                false
                            )
                        )
                    },
                    onUserClick = { userId -> onAction(FriendSuggestionAction.OnUserClick(userId)) }
                )
            }

            item {
                PeopleYouMayKnowHeader()
            }

            items(filterSuggestedFriends(state.users, state.relations)) { user ->
                val relation = state.relations[user.id] ?: RelationInfo(id = user.id ?: "")
                SuggestedFriendItem(
                    user = user,
                    relation = relation,
                    onSendRequest = { onAction(FriendSuggestionAction.SendRequest(user.id ?: "")) },
                    onUnSendRequest = {
                        onAction(
                            FriendSuggestionAction.UnSendRequest(
                                user.id ?: ""
                            )
                        )
                    },
                    onRemove = { onAction(FriendSuggestionAction.OnRemove(user.id ?: "")) },
                    onUserClick = { onAction(FriendSuggestionAction.OnUserClick(user.id ?: "")) }
                )
            }

            // Loading indicator
            item {
                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            // Error message
            item {
                state.error?.let { errorMessage ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

private fun filterPendingRequests(users: List<User>, relations: Map<String, RelationInfo>): List<User> {
    return users.filter { user ->
        val relation = relations[user.id]
        relation?.status == RelationshipStatus.PENDING_INCOMING
    }
}

private fun filterSuggestedFriends(users: List<User>, relations: Map<String, RelationInfo>): List<User> {
    return users.filter { user ->
        val relation = relations[user.id]
        relation == null || relation.status == RelationshipStatus.NONE || relation.status == RelationshipStatus.PENDING_OUTGOING
    }
}

@Preview(showBackground = true)
@Composable
private fun FriendSuggestionPreview() {
    MainAppTheme {
        Box {
            FriendSuggestionScreen(
                state = FriendSuggestionState(
                    users = MockData.mockUsers,
                    relations = MockData.mockRelations
                ),
                onAction = {}
            )
        }
    }
}