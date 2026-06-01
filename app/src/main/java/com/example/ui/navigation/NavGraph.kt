package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.home.HomeScreen
import com.example.ui.details.MovieDetailsScreen
import com.example.ui.search.SearchScreen
import com.example.ui.details.TvDetailsScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onMovieClick = { movieId ->
                    navController.navigate("movie_details/$movieId")
                },
                onTvShowClick = { tvId ->
                    navController.navigate("tv_details/$tvId")
                },
                onNavigateToSearch = {
                    navController.navigate("search")
                }
            )
        }
        composable("search") {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onMovieClick = { movieId ->
                    navController.navigate("movie_details/$movieId")
                },
                onTvShowClick = { tvId ->
                    navController.navigate("tv_details/$tvId")
                }
            )
        }
        composable(
            route = "movie_details/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
            MovieDetailsScreen(
                movieId = movieId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetails = { id -> 
                    navController.navigate("movie_details/$id") 
                }
            )
        }
        composable(
            route = "tv_details/{tvId}",
            arguments = listOf(navArgument("tvId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tvId = backStackEntry.arguments?.getInt("tvId") ?: 0
            TvDetailsScreen(
                tvId = tvId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetails = { id -> 
                    navController.navigate("tv_details/$id") 
                }
            )
        }
    }
}
