package com.tvstreaming.app.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    object Splash : Screen("splash")
    
    object Auth : Screen("auth")
    
    object Home : Screen("home")
    
    object Search : Screen("search")
    
    object Player : Screen(
        route = "player/{contentId}",
        arguments = listOf(
            navArgument("contentId") {
                type = NavType.StringType
            }
        )
    ) {
        fun createRoute(contentId: String) = "player/$contentId"
    }
    
    object Category : Screen(
        route = "category/{categoryId}",
        arguments = listOf(
            navArgument("categoryId") {
                type = NavType.StringType
            }
        )
    ) {
        fun createRoute(categoryId: String) = "category/$categoryId"
    }
    
    object Channels : Screen("channels")
    
    object Movies : Screen("movies")
    
    object Series : Screen("series")
    
    object Animation : Screen("animation")
    
    object ContentDetail : Screen(
        route = "content_detail/{contentId}",
        arguments = listOf(
            navArgument("contentId") {
                type = NavType.StringType
            }
        )
    ) {
        fun createRoute(contentId: String) = "content_detail/$contentId"
    }
}