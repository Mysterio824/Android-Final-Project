package com.androidfinalproject.hacktok.ui.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.enums.RelationshipStatus
import com.androidfinalproject.hacktok.ui.chat.component.ChatBubble
import com.androidfinalproject.hacktok.ui.chat.component.MessageInput
import com.androidfinalproject.hacktok.ui.chat.component.ChatTopBar
import com.androidfinalproject.hacktok.ui.chat.component.SearchBar
import kotlinx.coroutines.delay

@Composable
fun ChatScreen(
    state: ChatState,
    onAction: (ChatAction) -> Unit,
) {
    var messageText by remember { mutableStateOf(TextFieldValue("")) }
    val listState = rememberLazyListState()
    val searchFocusRequester = remember { FocusRequester() }

    // Effect to scroll to the highlighted message when search results change
    val highlightedMessage = if (state.searchResults.isNotEmpty() && state.currentSearchIndex >= 0) {
        state.searchResults[state.currentSearchIndex]
    } else null

    LaunchedEffect(highlightedMessage) {
        highlightedMessage?.let { message ->
            // Find the index of the message in the original list
            val index = state.messages.sortedByDescending { it.createdAt }
                .indexOfFirst { it.id == message.id }

            if (index >= 0) {
                // Delay to ensure animation is smooth
                delay(100)
                listState.animateScrollToItem(index)
            }
        }
    }

    // Effect to focus the search bar when search mode is enabled
    LaunchedEffect(state.isSearchMode) {
        if (state.isSearchMode) {
            delay(100) // Small delay to ensure the UI is ready
            searchFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(state.messages) {
        onAction(ChatAction.LoadInitialMessages)
    }

    if(state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp
            )
        }
        return
    } else if(state.otherUser != null && state.currentUser != null){
        // This code runs only if state.isLoading is false
        val isBlock = state.relation.status == RelationshipStatus.BLOCKED ||
                state.relation.status == RelationshipStatus.BLOCKING

        Scaffold(
            topBar = {
                if (state.isSearchMode) {
                    SearchBar(
                        query = state.searchQuery,
                        onQueryChange = { onAction(ChatAction.UpdateSearchQuery(it)) },
                        onClose = { onAction(ChatAction.SetSearchMode(false)) },
                        onNext = { onAction(ChatAction.SearchNext) },
                        onPrevious = { onAction(ChatAction.SearchPrevious) },
                        resultsCount = state.searchResults.size,
                        currentIndex = state.currentSearchIndex,
                        focusRequester = searchFocusRequester
                    )
                } else {
                    ChatTopBar(
                        otherUser = state.otherUser,
                        onBackClick = { onAction(ChatAction.NavigateBack) },
                        onInfoClick = { onAction(ChatAction.ChatOptionNavigate(state.otherUser.id!!)) },
                        onUserClick = { onAction(ChatAction.NavigateToManageUser(state.otherUser.id)) },
                        isBlock = isBlock,
                        onSearchClick = { onAction(ChatAction.SetSearchMode(true)) }
                    )
                }
            },
            bottomBar = {
                if (!state.isSearchMode) {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        MessageInput(
                            text = messageText,
                            onTextChanged = { messageText = it },
                            onSendClicked = {
                                if (messageText.text.isNotEmpty()) {
                                    onAction(ChatAction.SendMessage(messageText.text))
                                    messageText = TextFieldValue("")
                                }
                            },
                            onAction = onAction,
                            isBlock = isBlock
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            val sortedMessages = state.messages.sortedByDescending { it.createdAt }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                reverseLayout = true
            ) {
                items(sortedMessages) { message ->
                    val isHighlighted = highlightedMessage?.id == message.id

                    ChatBubble(
                        message = message,
                        isCurrentUser = message.senderId == state.currentUser.id,
                        onDeleteMessage = { onAction(ChatAction.DeleteMessage(message.id)) },
                        isHighlighted = isHighlighted
                    )
                }
            }
        }
    }
}