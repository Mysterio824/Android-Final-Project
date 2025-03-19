package com.androidfinalproject.hacktok.router.routes

import kotlinx.serialization.Serializable

@Serializable
sealed interface MainRoute : Route {
    @Serializable
    data object Graph : MainRoute {
        override val route = "main_graph"
    }
    @Serializable
    data object SearchDashboard : MainRoute {
        override val route = "search_dashboard"
    }
}