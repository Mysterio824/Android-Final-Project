package com.androidfinalproject.hacktok.router.routes

import kotlinx.serialization.Serializable

@Serializable
sealed interface AdminRoute : Route {
    @Serializable
    data object AdminManagement : AuthRoute {
        override val route = "admin_management"
    }
}