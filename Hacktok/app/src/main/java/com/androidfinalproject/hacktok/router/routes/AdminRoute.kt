package com.androidfinalproject.hacktok.router.routes

import kotlinx.serialization.Serializable

@Serializable
sealed interface AdminRoute : Route {
    @Serializable
    data object Graph : AdminRoute {
        override val route = "admin_management"
    }

    @Serializable
    data object AdminDashboard : AdminRoute {
        override val route = "admin_dashboard"
    }

    @Serializable
    data object CommentStatistic : AdminRoute {
        override val route = "comment_statistic"
    }

    @Serializable
    data object ReportStatistic : AdminRoute {
        override val route = "report_statistic"
    }

    @Serializable
    data object UserStatistic : AdminRoute {
        override val route = "user_statistic"
    }

    @Serializable
    data object PostStatistic : AdminRoute {
        override val route = "post_statistic"
    }
}