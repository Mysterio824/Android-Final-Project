package com.androidfinalproject.hacktok.ui.editProfile

import androidx.lifecycle.ViewModel
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditProfileViewModel(user: User) : ViewModel() {
    private val _username = MutableStateFlow(user.username)
    val username = _username.asStateFlow()

    private val _fullName = MutableStateFlow(user.fullName ?: "Unknown")
    val fullName = _fullName.asStateFlow()

    private val _email = MutableStateFlow(user.email)
    val email = _email.asStateFlow()

    private val _bio = MutableStateFlow(user.bio ?: "")
    val bio = _bio.asStateFlow()

    private val _role = MutableStateFlow(user.role)
    val role = _role.asStateFlow()

    private val _errorState = MutableStateFlow(mapOf<String, Boolean>())
    val errorState = _errorState.asStateFlow()

    fun updateField(field: String, value: String) {
        when (field) {
            "username" -> _username.value = value
            "fullName" -> _fullName.value = value
            "email" -> _email.value = value
            "bio" -> _bio.value = value
            "role" -> _role.value = UserRole.valueOf(value)
        }
    }

    private fun validateFields(): Boolean {
        val errors = mutableMapOf<String, Boolean>()
        errors["username"] = _username.value.isBlank()
        errors["fullName"] = _fullName.value.isBlank()
        errors["email"] = _email.value.isBlank()
        errors["bio"] = _bio.value.isBlank()

        _errorState.value = errors
        return !errors.containsValue(true)
    }

    fun saveProfile() {
        if (validateFields()) {
            // Call backend API or store data
        }
    }
}