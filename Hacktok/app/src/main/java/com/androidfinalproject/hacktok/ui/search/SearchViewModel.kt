package com.androidfinalproject.hacktok.ui.search

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
    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    init {
        loadData()
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

    fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.UpdateQuery -> {
                _state.update {
                    it.copy(searchQuery = action.query)
                }
                performSearch()
            }
            is SearchAction.ChangeTab -> {
                _state.update {
                    it.copy(selectedTabIndex = action.tabIndex)
                }
                performSearch()
            }
            is SearchAction.OnPostClick -> {}
            is SearchAction.OnUserClick -> {}
            else -> {}
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
                            isLoading = false
                        )
                    }
                    return@launch
                }

                when (_state.value.selectedTabIndex) {
                    0 -> {
                        // Search users by all available fields
                        val users = userRepository.searchUsers(query)
                        val filteredUsers = users.filter { user ->
                            user.username?.lowercase()?.contains(query) == true ||
                            user.fullName?.lowercase()?.contains(query) == true ||
                            user.email?.lowercase()?.contains(query) == true ||
                            user.bio?.lowercase()?.contains(query) == true ||
                            user.id?.lowercase()?.contains(query) == true
                        }
                        _state.update {
                            it.copy(
                                users = users,
                                filteredUsers = filteredUsers,
                                filteredPosts = emptyList(),
                                isLoading = false
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
                                isLoading = false
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
                                isLoading = false
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
                                isLoading = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
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