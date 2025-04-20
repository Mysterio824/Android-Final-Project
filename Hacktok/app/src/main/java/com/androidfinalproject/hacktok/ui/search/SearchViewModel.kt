package com.androidfinalproject.hacktok.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.repository.PostRepository
import com.androidfinalproject.hacktok.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) : ViewModel() {
    private val TAG = "SearchViewModel"
    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    init {
        loadData()
        loadSearchHistory()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                // Load users from repository
                val users = userRepository.getCurrentUser()?.let { currentUser ->
                    // Get all users except current user
                    // Note: In a real app, you might want to implement a proper search API
                    // This is just a placeholder implementation
                    listOf(currentUser) // Replace with actual user search
                } ?: emptyList()

                // Load posts from repository
                val posts = emptyList<Post>() // Replace with actual post search

                _state.update {
                    it.copy(
                        users = users,
                        posts = posts,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load data: ${e.message}"
                    )
                }
            }
        }
    }

    private fun loadSearchHistory() {
        viewModelScope.launch {
            try {
                val history = userRepository.getSearchHistory()
                Log.d(TAG, "Loaded search history: ${history.joinToString()}")
                _state.update { 
                    it.copy(
                        searchHistory = history,
                        // Ensure search history is shown when query is empty
                        showSearchHistory = it.searchQuery.isEmpty()
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading search history", e)
                // Just log the error, don't show to user as it's not critical
                e.printStackTrace()
            }
        }
    }
    
    private fun clearSearchHistory() {
        viewModelScope.launch {
            try {
                userRepository.clearSearchHistory()
                _state.update { it.copy(searchHistory = emptyList()) }
                Log.d(TAG, "Search history cleared")
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing search history", e)
                _state.update {
                    it.copy(error = "Failed to clear search history: ${e.message}")
                }
            }
        }
    }
    
    // Save current search query to history
    private fun saveCurrentSearchToHistory() {
        val query = _state.value.searchQuery
        if (query.isNotBlank()) {
            viewModelScope.launch {
                try {
                    Log.d(TAG, "Saving search query to history: $query")
                    userRepository.addSearchQuery(query)
                    // We need to reload history to get the updated list
                    loadSearchHistory()
                } catch (e: Exception) {
                    Log.e(TAG, "Error saving search query to history", e)
                }
            }
        }
    }

    fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.UpdateQuery -> {
                val query = action.query
                _state.update {
                    it.copy(
                        searchQuery = query,
                        showSearchHistory = query.isEmpty()
                    )
                }
                if (query.isNotBlank()) {
                    performSearch()
                }
            }
            is SearchAction.ChangeTab -> {
                _state.update {
                    it.copy(selectedTabIndex = action.tabIndex)
                }
                if (_state.value.searchQuery.isNotBlank()) {
                    performSearch()
                }
            }
            is SearchAction.OnPostClick -> {
                // Save search to history when user clicks on a post
                Log.d(TAG, "User clicked on post, saving search query to history")
                saveCurrentSearchToHistory()
            }
            is SearchAction.OnUserClick -> {
                // Save search to history when user clicks on a user profile
                Log.d(TAG, "User clicked on profile, saving search query to history")
                saveCurrentSearchToHistory()
            }
            is SearchAction.OnNavigateBack -> {
                // Save search to history when user navigates back
                Log.d(TAG, "User navigated back, saving search query to history")
                saveCurrentSearchToHistory()
            }
            is SearchAction.LoadSearchHistory -> {
                Log.d(TAG, "Loading search history")
                loadSearchHistory()
            }
            is SearchAction.ClearSearchHistory -> {
                Log.d(TAG, "Clearing search history")
                clearSearchHistory()
            }
            is SearchAction.OnHistoryItemClick -> {
                Log.d(TAG, "User clicked on history item: ${action.query}")
                _state.update {
                    it.copy(searchQuery = action.query, showSearchHistory = false)
                }
                performSearch()
            }
            is SearchAction.ToggleSearchHistoryVisibility -> {
                _state.update {
                    it.copy(showSearchHistory = !it.showSearchHistory)
                }
            }
        }
    }

    private fun performSearch() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val query = _state.value.searchQuery.lowercase()
                if (query.isBlank()) {
                    _state.update {
                        it.copy(
                            filteredUsers = emptyList(),
                            filteredPosts = emptyList(),
                            isLoading = false,
                            showSearchHistory = true
                        )
                    }
                    return@launch
                }

                // Don't save the search term yet - it will be saved when a user clicks on a result
                when (_state.value.selectedTabIndex) {
                    0 -> {
                        // Search users by all available fields
                        val users = userRepository.searchUsers(query)
                        
                        val filteredUsers = users.filter { user ->
                            user.username?.lowercase()?.contains(query) == true ||
                            user.fullName?.lowercase()?.contains(query) == true ||
                            user.email.lowercase().contains(query) ||
                            user.bio?.lowercase()?.contains(query) == true ||
                            user.id?.lowercase()?.contains(query) == true
                        }
                        _state.update {
                            it.copy(
                                users = users,
                                filteredUsers = filteredUsers,
                                filteredPosts = emptyList(),
                                isLoading = false,
                                showSearchHistory = false
                            )
                        }
                    }
                    1 -> {
                        // Search posts with hashtags
                        val posts = postRepository.searchPosts(query)
                            .filter { post -> post.content.contains("#") }
                        _state.update {
                            it.copy(
                                posts = posts,
                                filteredPosts = posts,
                                filteredUsers = emptyList(),
                                isLoading = false,
                                showSearchHistory = false
                            )
                        }
                    }
                    2 -> {
                        // Search posts with places
                        val posts = postRepository.searchPosts(query)
                            .filter { post -> post.content.contains("place") }
                        _state.update {
                            it.copy(
                                posts = posts,
                                filteredPosts = posts,
                                filteredUsers = emptyList(),
                                isLoading = false,
                                showSearchHistory = false
                            )
                        }
                    }
                    3 -> {
                        // Search all posts
                        val posts = postRepository.searchPosts(query)
                        _state.update {
                            it.copy(
                                posts = posts,
                                filteredPosts = posts,
                                filteredUsers = emptyList(),
                                isLoading = false,
                                showSearchHistory = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error performing search", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to perform search: ${e.message}"
                    )
                }
            }
        }
    }
}