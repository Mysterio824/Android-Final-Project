package com.androidfinalproject.hacktok.router.routes

import kotlinx.serialization.Serializable

@Serializable
sealed interface MainRoute : Route {
    @Serializable
    data object MainGraph : AuthRoute {
        override val route = "main_graph"
    }

}