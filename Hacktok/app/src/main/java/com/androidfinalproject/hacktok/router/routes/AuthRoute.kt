package com.androidfinalproject.hacktok.router.routes

import kotlinx.serialization.Serializable

@Serializable
sealed interface AuthRoute : Route {
    @Serializable
    data object Graph : AuthRoute {
        override val route = "auth_graph"
    }

    @Serializable
    data object Login : AuthRoute {
        override val route = "login"
    }

    @Serializable
    data object ForgotPassword : AuthRoute {
        override val route = "password_recovery"
    }
}