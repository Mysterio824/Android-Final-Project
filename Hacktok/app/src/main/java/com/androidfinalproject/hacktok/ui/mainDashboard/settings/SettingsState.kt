package com.androidfinalproject.hacktok.ui.mainDashboard.settings

import com.androidfinalproject.hacktok.model.User

data class SettingsState (
    val language: String = "",
    val isLanguageChanged: Boolean = false,
    val isLogout: Boolean = false,
    val currentUser: User? = null
)