package com.androidfinalproject.hacktok.ui.mainDashboard

import com.androidfinalproject.hacktok.model.User

data class DashboardState (
    val selectedTab: String = "Home",
    val currentUser: User? = null,
    val isLogout: Boolean = false,
    val isLoading: Boolean = true,
)