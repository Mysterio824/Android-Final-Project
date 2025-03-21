package com.androidfinalproject.hacktok.router.routes

import kotlinx.serialization.Serializable

@Serializable
sealed interface DashboardRoute : Route {
    @Serializable
    data object Graph : DashboardRoute {
        override val route = "dashboard_graph"
    }

    @Serializable
    data object Main : DashboardRoute {
        override val route = "dashboard_main"
    }
}
