package com.androidfinalproject.hacktok.ui.secretCrush

import com.androidfinalproject.hacktok.model.User

data class SecretCrushState(
    val currentUser: User? = null,
    val selectedCrushes: List<SelectedCrush> = emptyList(),
    val availableUsers: List<User> = emptyList(),  // Added list of available users
    val peopleWhoLikeYou: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class SelectedCrush(
    val user: User,
    val message: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)