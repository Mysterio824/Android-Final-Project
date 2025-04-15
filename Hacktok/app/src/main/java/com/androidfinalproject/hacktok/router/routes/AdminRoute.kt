package com.androidfinalproject.hacktok.router.routes

import kotlinx.serialization.Serializable

@Serializable
sealed interface AdminRoute : Route {
    @Serializable
    data object Graph : AdminRoute {
        override val route = "admin_graph"
    }

    @Serializable
    data object AdminDashboard : AdminRoute {
        override val route = "adminManagement"
    }

    @Serializable
    data object Statistic : AdminRoute {
        override val route = "statistics"
    }

    @Serializable
    data object UserDetail : AdminRoute {
        override val route = "userDetail/{userId}"

        fun createRoute(userId: String): String {
            return "userDetail/$userId"
        }
    }
}