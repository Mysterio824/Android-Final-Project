package com.androidfinalproject.hacktok.ui.secretCrush

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.SecretCrush
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.repository.SecretCrushRepository
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
    private val secretCrushRepository: SecretCrushRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SecretCrushState())
    val state: StateFlow<SecretCrushState> = _state.asStateFlow()

    init {
        loadCrushes()
    }

    fun onAction(action: SecretCrushAction) {
        when (action) {
            is SecretCrushAction.SelectUser -> action.user.id?.let { sendCrush(it) }
            is SecretCrushAction.RevealCrush -> revealCrush(action.crushId)
            is SecretCrushAction.UnselectUser -> deleteCrush(action.userId)
            SecretCrushAction.LoadCrushData -> loadCrushes()
            SecretCrushAction.NavigateBack -> {} // Handled by the screen root
            is SecretCrushAction.SendMessage -> TODO()
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
                                    timestamp = crush.createdAt.time
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
                                    timestamp = crush.createdAt.time
                                )
                            }
                            _state.update { it.copy(receivedCrushes = receivedCrushes) }
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

            try {
                secretCrushRepository.revealSecretCrush(crushId).collect { result ->
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

    private fun deleteCrush(crushId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                secretCrushRepository.deleteSecretCrush(crushId).collect { result ->
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
}