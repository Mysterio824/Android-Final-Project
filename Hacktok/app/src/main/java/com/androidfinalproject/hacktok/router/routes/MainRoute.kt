package com.androidfinalproject.hacktok.router.routes

import kotlinx.serialization.Serializable

@Serializable
sealed interface MainRoute : Route {
    class MainGraph {
        companion object {
            val route: String
                get() {
                    TODO()
                }
        }

    }

    @Serializable
    data object Graph : MainRoute {
        override val route = "main_graph"
    }
    @Serializable
    data object SearchDashboard : MainRoute {
        override val route = "search_dashboard"
    }
}