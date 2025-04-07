package com.androidfinalproject.hacktok.router.routes

import kotlinx.serialization.Serializable

@Serializable
sealed interface AdminRoute : Route {
    @Serializable
    data object Graph : AdminRoute {
        override val route = "admin_management"
    }

    @Serializable
    data object AdminDashboard : AdminRoute {
        override val route = "admin_dashboard"
    }

    @Serializable
    data object Statistic : AdminRoute {
        override val route = "statistic"
    }
}