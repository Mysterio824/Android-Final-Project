package com.androidfinalproject.hacktok.router.routes

import kotlinx.serialization.Serializable

@Serializable
sealed interface MainRoute : Route {
    @Serializable
    data object Graph : MainRoute {
        override val route = "main_graph"
    }

    @Serializable
    data object PostDetail : MainRoute {
        override val route = "post_detail"
    }

    @Serializable
    data object UserDetail : MainRoute {
        override val route = "user_detail"
    }

    @Serializable
    data object FriendList : MainRoute {
        override val route = "friend_list"
    }

    @Serializable
    data object ChatRoom : MainRoute {
        override val route = "chat_room"
    }

    @Serializable
    data object EditProfile : MainRoute {
        override val route = "edit_profile"
    }

    @Serializable
    data object EditPost : MainRoute {
        override val route = "edit_post"
    }

}