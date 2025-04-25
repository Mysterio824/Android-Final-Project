package com.androidfinalproject.hacktok.ui.secretCrush

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.SecretCrush
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.repository.SecretCrushRepository
import com.androidfinalproject.hacktok.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import javax.inject.Inject


@HiltViewModel
class SecretCrushViewModel @Inject constructor(
    private val secretCrushRepository: SecretCrushRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SecretCrushState())
    val state: StateFlow<SecretCrushState> = _state.asStateFlow()

    init {
        loadCrushes()
        loadAllUsers()
    }

    fun onAction(action: SecretCrushAction) {
        when (action) {
            is SecretCrushAction.SelectUser -> action.user.id?.let { sendCrush(it) }
            is SecretCrushAction.RevealCrush -> revealCrush(action.crushId)
            is SecretCrushAction.UnselectUser -> deleteCrush(action.userId)
            SecretCrushAction.LoadCrushData -> loadCrushes()
            SecretCrushAction.LoadAllUsers -> loadAllUsers()
            SecretCrushAction.NavigateBack -> {} // Handled by the screen root
            is SecretCrushAction.SendMessage -> TODO()
        }
    }

    private fun loadAllUsers() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val users = userRepository.getAllUsers()
                _state.update { it.copy(availableUsers = users) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun loadCrushes() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                // Launch separate coroutines for each flow
                launch {
                    secretCrushRepository.observeMySecretCrushes().collect { result ->
                        result.onSuccess { crushes ->
                            val selectedCrushes = crushes.map { crush ->
                                SelectedCrush(
                                    user = User(
                                        id = crush.receiverId,
                                        email = "",
                                        username = crush.receiverName,
                                        fullName = crush.receiverName,
                                        profileImage = crush.receiverImageUrl
                                    ),
                                    message = null,
                                    timestamp = crush.createdAt.time,
                                    crushId = crush.id,
                                    isRevealed = crush.revealed
                                )
                            }
                            _state.update { it.copy(selectedCrushes = selectedCrushes) }
                        }.onFailure { error ->
                            _state.update { it.copy(error = error.message) }
                        }
                    }
                }

                launch {
                    secretCrushRepository.observeReceivedSecretCrushes().collect { result ->
                        result.onSuccess { crushes ->
                            val receivedCrushes = crushes.map { crush ->
                                SelectedCrush(
                                    user = User(
                                        id = crush.senderId,
                                        email = "",
                                        username = crush.senderName,
                                        fullName = crush.senderName,
                                        profileImage = crush.senderImageUrl
                                    ),
                                    message = null,
                                    timestamp = crush.createdAt.time,
                                    crushId = crush.id,
                                    isRevealed = crush.revealed
                                )
                            }
                            // Update peopleWhoLikeYou count
                            val peopleWhoLikeYouCount = crushes.size
                            _state.update { 
                                it.copy(
                                    receivedCrushes = receivedCrushes,
                                    peopleWhoLikeYou = peopleWhoLikeYouCount
                                ) 
                            }
                        }.onFailure { error ->
                            _state.update { it.copy(error = error.message) }
                        }
                    }
                }

                // Set loading to false after initial data is received
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun sendCrush(crushId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                secretCrushRepository.sendSecretCrush(crushId).collect { result ->
                    result.onSuccess {
                        loadCrushes()
                    }.onFailure { error ->
                        _state.update { it.copy(error = error.message) }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun revealCrush(crushId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            Log.d("SecretCrushViewModel", "Attempting to reveal crush with ID: $crushId")

            try {
                secretCrushRepository.revealSecretCrush(crushId).collect { result ->
                    result.onSuccess {
                        Log.d("SecretCrushViewModel", "Successfully revealed crush with ID: $crushId")

                        loadCrushes()
                    }.onFailure { error ->
                        Log.e("SecretCrushViewModel", "Failed to reveal crush with ID: $crushId", error)
                        _state.update { it.copy(error = error.message,isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                Log.e("SecretCrushViewModel", "Exception while revealing crush with ID: $crushId", e)
                _state.update { it.copy(error = e.message,isLoading = false) }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun deleteCrush(crushId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            Log.d("SecretCrushViewModel", "Attempting to delete crush with ID: $crushId")

            try {
                secretCrushRepository.deleteSecretCrush(crushId).collect { result ->
                    result.onSuccess {
                        Log.d("SecretCrushViewModel", "Successfully deleted crush with ID: $crushId")
                        loadCrushes()
                    }.onFailure { error ->
                        Log.e("SecretCrushViewModel", "Failed to delete crush with ID: $crushId", error)
                        _state.update { it.copy(error = error.message, isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                Log.e("SecretCrushViewModel", "Exception while deleting crush with ID: $crushId", e)
                _state.update { it.copy(error = e.message, isLoading = false) }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }
}