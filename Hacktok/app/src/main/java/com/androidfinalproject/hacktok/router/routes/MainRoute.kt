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
    data object Profile : MainRoute {
        override val route = "profile"
    }

    @Serializable
    data object FriendList : MainRoute {
        override val route = "friend_list"
    }

    @Serializable
    data object Search : MainRoute {
        override val route = "search_screen"
    }

    @Serializable
    data object ChatDashboard : MainRoute {
        override val route = "chat_dashboard"
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

    @Serializable
    data object NewStory : MainRoute {
        override val route = "new_story"
    }

    @Serializable
    data class NewPost(val postId: String? = null) : MainRoute {
        override val route: String
            get() = if (postId != null) "$BASE_ROUTE?postId=$postId" else BASE_ROUTE

        companion object {
            const val BASE_ROUTE = "new_post"
        }
    }
}