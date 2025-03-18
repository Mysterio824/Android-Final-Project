package com.androidfinalproject.hacktok.router.routes

import kotlinx.serialization.Serializable

@Serializable
sealed interface FriendListRoute : Route {
    @Serializable
    data object Graph : AuthRoute {
        override val route = "auth_graph"
    }

    @Serializable
    data object FriendList : AuthRoute {
        override val route = "login"
    }
}