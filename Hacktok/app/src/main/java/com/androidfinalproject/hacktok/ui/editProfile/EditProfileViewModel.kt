package com.androidfinalproject.hacktok.ui.editProfile

import androidx.lifecycle.ViewModel
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EditProfileViewModel(userId: String) : ViewModel() {
    private val _state = MutableStateFlow( EditProfileState() )
    val state = _state.asStateFlow()

    init{
        val user = MockData.mockUsers.first()
        _state.update {
            it.copy(
                username = user.username,
                fullName = user.fullName ?: "Unknown",
                email = user.email,
                bio = user.bio ?: "",
                role = user.role,
                errorState = emptyMap()
            )
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
                    // Call backend API or store data
                    // For now, we'll just log the updated state
                    println("Saving profile: ${_state.value}")
                }
            }
            EditProfileAction.Cancel -> {
                // Reset fields or navigate back
                // For now, we'll just log the action
                println("Cancel action triggered")
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