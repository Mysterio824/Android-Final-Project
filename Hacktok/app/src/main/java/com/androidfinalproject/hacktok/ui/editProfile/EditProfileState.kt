package com.androidfinalproject.hacktok.ui.editProfile

import com.androidfinalproject.hacktok.model.UserRole

data class EditProfileState(
    val username: String = "",
    val fullName: String = "",
    val email: String = "",
    val bio: String = "",
    val role: UserRole = UserRole.USER,
    val errorState: Map<String, Boolean> = emptyMap(),
    val isLoading: Boolean = true,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)