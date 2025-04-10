package com.androidfinalproject.hacktok.ui.mainDashboard

import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.User

data class DashboardState (
    val user: User = MockData.mockUsers.first(),
    val selectedTab: String = "Home"
)