package com.androidfinalproject.hacktok.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.friendList.FriendListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SearchViewModel : ViewModel() {
    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _state.update {
            it.copy(
                users = MockData.mockUsers,
                posts = MockData.mockPosts
            )
        }
    }

    fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.UpdateQuery -> {
                _state.update {
                    it.copy(searchQuery = action.query)
                }
                filterResults()
            }
            is SearchAction.ChangeTab -> {
                _state.update {
                    it.copy(selectedTabIndex = action.tabIndex)
                }
                filterResults()
            }

            is SearchAction.OnPostClick -> {}
            is SearchAction.OnUserClick -> {}
        }
    }


    private fun filterResults() {
        val query = _state.value.searchQuery.lowercase()

        _state.update {
            val filteredPosts = when (_state.value.selectedTabIndex) {
                1 -> it.posts.filter { post -> post.content.lowercase().contains(query) && post.content.contains("#") }
                2 -> it.posts.filter { post -> post.content.lowercase().contains(query) && post.content.contains("place") }
                3 -> it.posts.filter { post -> post.content.lowercase().contains(query) }
                else -> emptyList()
            }

            it.copy(
                filteredUsers = if (_state.value.selectedTabIndex == 0) it.users.filter { user -> user.username.lowercase().contains(query) } else emptyList(),
                filteredPosts = filteredPosts
            )
        }
    }

}