package com.itb.visualizadorpostsapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.itb.visualizadorpostsapp.ui.screens.postdetail.PostDetailScreen
import com.itb.visualizadorpostsapp.ui.screens.postlist.PostListScreen

/**
 * Clase sellada para definir las rutas de navegación
 */
sealed class Screen(val route: String) {
    object PostList : Screen("post_list")
    object PostDetail : Screen("post_detail/{postId}") {
        fun createRoute(postId: Int) = "post_detail/$postId"
    }
}

/**
 * Grafo de navegación de la aplicación
 */
@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.PostList.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Pantalla de lista de posts
        composable(route = Screen.PostList.route) {
            PostListScreen(
                onPostClick = { postId ->
                    navController.navigate(Screen.PostDetail.createRoute(postId))
                }
            )
        }

        // Pantalla de detalle del post
        composable(
            route = Screen.PostDetail.route,
            arguments = listOf(
                navArgument("postId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getInt("postId") ?: return@composable
            PostDetailScreen(
                postId = postId,
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
    }
}