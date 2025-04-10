package com.androidfinalproject.hacktok.router.routes

import kotlinx.serialization.Serializable

@Serializable
sealed interface TestRoute : Route {
    @Serializable
    data object Graph : TestRoute {
        override val route = "test_route"
    }

    @Serializable
    data object screen1 : TestRoute {
        override val route = "screen_1"
    }

    @Serializable
    data object screen2 : TestRoute {
        override val route = "screen_2"
    }

    @Serializable
    data object screen3 : TestRoute {
        override val route = "screen_3"
    }
}