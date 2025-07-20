package com.tvstreaming.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tvstreaming.app.ui.screens.animation.AnimationScreen
import com.tvstreaming.app.ui.screens.auth.AuthScreen
import com.tvstreaming.app.ui.screens.channels.ChannelsScreen
import com.tvstreaming.app.ui.screens.detail.ContentDetailScreen
import com.tvstreaming.app.ui.screens.home.HomeScreen
import com.tvstreaming.app.ui.screens.movies.MoviesScreen
import com.tvstreaming.app.ui.screens.player.PlayerScreen
import com.tvstreaming.app.ui.screens.search.SearchScreen
import com.tvstreaming.app.ui.screens.series.SeriesScreen
import com.tvstreaming.app.ui.screens.splash.SplashScreen

@Composable
fun TVStreamingNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToAuth = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Auth.route) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToPlayer = { contentId ->
                    navController.navigate(Screen.ContentDetail.createRoute(contentId))
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                },
                onNavigateToChannels = {
                    navController.navigate(Screen.Channels.route)
                },
                onNavigateToMovies = {
                    navController.navigate(Screen.Movies.route)
                },
                onNavigateToSeries = {
                    navController.navigate(Screen.Series.route)
                },
                onNavigateToAnimation = {
                    navController.navigate(Screen.Animation.route)
                }
            )
        }
        
        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateToPlayer = { contentId ->
                    navController.navigate(Screen.ContentDetail.createRoute(contentId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.Player.route,
            arguments = Screen.Player.arguments
        ) { backStackEntry ->
            val contentId = backStackEntry.arguments?.getString("contentId") ?: ""
            PlayerScreen(
                contentId = contentId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Channels.route) {
            ChannelsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Movies.route) {
            MoviesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onMovieClick = { movieId ->
                    navController.navigate(Screen.ContentDetail.createRoute(movieId))
                }
            )
        }
        
        composable(Screen.Series.route) {
            SeriesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSeriesClick = { seriesId ->
                    navController.navigate(Screen.ContentDetail.createRoute(seriesId))
                }
            )
        }
        
        composable(Screen.Animation.route) {
            AnimationScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onAnimationClick = { animationId ->
                    navController.navigate(Screen.ContentDetail.createRoute(animationId))
                }
            )
        }
        
        composable(
            route = Screen.ContentDetail.route,
            arguments = Screen.ContentDetail.arguments
        ) { backStackEntry ->
            val contentId = backStackEntry.arguments?.getString("contentId") ?: ""
            ContentDetailScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onPlayContent = { id ->
                    navController.navigate(Screen.Player.createRoute(id))
                }
            )
        }
    }
}