package com.androidfinalproject.hacktok.ui.editProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.enums.UserRole
import com.androidfinalproject.hacktok.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(EditProfileState())
    val state = _state.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val user = userRepository.getCurrentUser()
                if (user != null) {
                    _state.update {
                        it.copy(
                            username = user.username ?: "",
                            fullName = user.fullName ?: "Unknown",
                            email = user.email,
                            bio = user.bio ?: "",
                            role = user.role,
                            errorState = emptyMap(),
                            isLoading = false,
                            isSuccess = false,
                            errorMessage = null
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = false,
                            errorMessage = "Failed to load user profile"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = "Error: ${e.message}"
                    )
                }
            }
        }
    }

    fun onAction(action: EditProfileAction) {
        when (action) {
            is EditProfileAction.UpdateField -> {
                when (action.field) {
                    "username" -> _state.update { it.copy(username = action.value) }
                    "fullName" -> _state.update { it.copy(fullName = action.value) }
                    "email" -> _state.update { it.copy(email = action.value) }
                    "bio" -> _state.update { it.copy(bio = action.value) }
                    "role" -> _state.update { it.copy(role = UserRole.valueOf(action.value)) }
                }
            }
            EditProfileAction.SaveProfile -> {
                if (validateFields()) {
                    saveProfile()
                }
            }
            EditProfileAction.Cancel -> {
                // Reset fields or navigate back
                loadCurrentUser()
            }
        }
    }

    private fun saveProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                val currentUser = userRepository.getCurrentUser()
                if (currentUser != null) {
                    val updatedUser = currentUser.copy(
                        username = _state.value.username,
                        fullName = _state.value.fullName,
                        email = _state.value.email,
                        bio = _state.value.bio,
                        role = _state.value.role
                    )
                    
                    val success = userRepository.updateUserProfile(updatedUser)
                    
                    if (success) {
                        _state.update { 
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                errorMessage = null
                            )
                        }
                    } else {
                        _state.update { 
                            it.copy(
                                isLoading = false,
                                isSuccess = false,
                                errorMessage = "Failed to update profile"
                            )
                        }
                    }
                } else {
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            isSuccess = false,
                            errorMessage = "User not found"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = "Error: ${e.message}"
                    )
                }
            }
        }
    }

    private fun validateFields(): Boolean {
        val errors = mutableMapOf<String, Boolean>()
        errors["username"] = _state.value.username.isBlank()
        errors["fullName"] = _state.value.fullName.isBlank()
        errors["email"] = _state.value.email.isBlank()
        errors["bio"] = _state.value.bio.isBlank()

        _state.update { it.copy(errorState = errors) }
        return !errors.containsValue(true)
    }
}