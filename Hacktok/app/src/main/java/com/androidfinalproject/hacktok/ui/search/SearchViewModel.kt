package com.androidfinalproject.hacktok.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User

class SearchViewModel : ViewModel() {

    var uiState by mutableStateOf(SearchUiState())
        private set

    init {
        loadData()
    }

    private fun loadData() {
        uiState = uiState.copy(
            users = MockData.mockUsers,
            posts = MockData.mockPosts
        )
    }

    fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.UpdateQuery -> {
                uiState = uiState.copy(searchQuery = action.query)
                filterResults()
            }
            is SearchAction.ChangeTab -> {
                uiState = uiState.copy(selectedTabIndex = action.tabIndex)
                filterResults()
            }
        }
    }

    private fun filterResults() {
        val query = uiState.searchQuery.lowercase()
        uiState = when (uiState.selectedTabIndex) {
            0 -> uiState.copy(
                filteredUsers = uiState.users.filter { it.username.lowercase().contains(query) }
            )
            1 -> uiState.copy(
                filteredPosts = uiState.posts.filter { it.content.lowercase().contains(query) && it.content.contains("#") }
            )
            2 -> uiState.copy(
                filteredPosts = uiState.posts.filter { it.content.lowercase().contains(query) && it.content.contains("place") }
            )
            3 -> uiState.copy(
                filteredPosts = uiState.posts.filter { it.content.lowercase().contains(query) }
            )
            else -> uiState
        }
    }
}