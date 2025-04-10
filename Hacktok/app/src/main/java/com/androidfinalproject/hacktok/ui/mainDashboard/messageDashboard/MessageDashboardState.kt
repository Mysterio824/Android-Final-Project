package com.androidfinalproject.hacktok.ui.mainDashboard.messageDashboard

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.androidfinalproject.hacktok.model.User

data class MessageDashboardState (
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: MutableState<String> = mutableStateOf(""),
    val userList: List<User> = emptyList()
)