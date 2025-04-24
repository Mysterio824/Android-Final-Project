package com.androidfinalproject.hacktok.ui.secretCrush

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecretCrushViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(SecretCrushState())
    val state = _state.asStateFlow()

    init {
        loadCrushData()
    }

    fun onAction(action: SecretCrushAction) {
        when (action) {
            is SecretCrushAction.LoadCrushData -> loadCrushData()
            is SecretCrushAction.SelectUser -> selectUser(action.user)
            is SecretCrushAction.UnselectUser -> unselectUser(action.userId)
            is SecretCrushAction.SendMessage -> sendCrushMessage(action.userId, action.message)
            else -> {} // Navigation actions are handled in SecretCrushScreenRoot
        }
    }

    private fun loadCrushData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // Using mock data for now
                val currentUser = MockData.mockUsers[0]
                val availableUsers = MockData.mockUsers.drop(1) // All users except current user

                _state.update {
                    it.copy(
                        currentUser = currentUser,
                        availableUsers = availableUsers,
                        isLoading = false,
                        peopleWhoLikeYou = 3, // Mock number
                        selectedCrushes = emptyList()
                    )
                }

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message ?: "An error occurred",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun selectUser(user: User) {
        val currentCrushes = _state.value.selectedCrushes

        // Check if we already have 5 crushes
        if (currentCrushes.size >= 5) {
            _state.update { it.copy(error = "You can only select up to 5 secret crushes") }
            return
        }

        // Check if user is already selected
        if (currentCrushes.any { it.user.id == user.id }) {
            return
        }

        // Add the new crush to the list
        _state.update {
            it.copy(
                selectedCrushes = it.selectedCrushes + SelectedCrush(user)
            )
        }
    }

    private fun unselectUser(userId: String) {
        _state.update {
            it.copy(
                selectedCrushes = it.selectedCrushes.filter { crush -> crush.user.id != userId }
            )
        }
    }

    private fun sendCrushMessage(userId: String, message: String) {
        // Just update the state for now
        _state.update { state ->
            state.copy(
                selectedCrushes = state.selectedCrushes.map { crush ->
                    if (crush.user.id == userId) {
                        crush.copy(message = message)
                    } else {
                        crush
                    }
                }
            )
        }
    }
}