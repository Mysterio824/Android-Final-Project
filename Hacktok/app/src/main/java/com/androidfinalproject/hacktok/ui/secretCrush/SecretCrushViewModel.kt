package com.androidfinalproject.hacktok.ui.secretCrush

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.repository.AuthRepository
import com.androidfinalproject.hacktok.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecretCrushViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(SecretCrushState())
    val state = _state.asStateFlow()

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
                val firebaseUser = authRepository.getCurrentUser()
                if (firebaseUser == null) {
                    _state.update { it.copy(error = "Not authenticated", isLoading = false) }
                    return@launch
                }

                // Convert FirebaseUser to our User model
                val currentUser = User.fromFirebaseUser(firebaseUser)

                // TODO: Load actual crush data and like count from backend
                // Placeholder for now - this will be implemented in backend later

                _state.update {
                    it.copy(
                        currentUser = currentUser,
                        isLoading = false,
                        // Placeholder data - will be replaced with actual API calls
                        peopleWhoLikeYou = 0,
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

        // TODO: Update the selection in the backend
    }

    private fun unselectUser(userId: String) {
        _state.update {
            it.copy(
                selectedCrushes = it.selectedCrushes.filter { crush -> crush.user.id != userId }
            )
        }

        // TODO: Update the unselection in the backend
    }

    private fun sendCrushMessage(userId: String, message: String) {
        // TODO: Send message to backend
        // This is just a placeholder as backend will be implemented later
    }
}