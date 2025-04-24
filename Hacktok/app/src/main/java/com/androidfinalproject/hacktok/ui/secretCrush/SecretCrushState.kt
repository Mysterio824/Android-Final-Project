package com.androidfinalproject.hacktok.ui.secretcrush

import com.androidfinalproject.hacktok.model.User

data class SecretCrushState(
    val currentUser: User? = null,
    val selectedCrushes: List<SelectedCrush> = emptyList(),
    val peopleWhoLikeYou: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showSelectDialog: Boolean = false
)

data class SelectedCrush(
    val user: User,
    val message: String = ""
)