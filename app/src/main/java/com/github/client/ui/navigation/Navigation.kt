package com.github.client.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.client.ui.screens.create_issue.CreateIssueScreen
import com.github.client.ui.screens.home.HomeScreen
import com.github.client.ui.screens.login.LoginScreen
import com.github.client.ui.screens.profile.ProfileScreen
import com.github.client.ui.screens.repository_details.RepositoryDetailsScreen
import com.github.client.ui.screens.search.SearchScreen

@Composable
fun AppNavigation(snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                navigateToRepositoryDetails = { owner, repo ->
                    navController.navigate(Screen.RepositoryDetails.createRoute(owner, repo))
                },
                navigateToSearch = {
                    navController.navigate(Screen.Search.route)
                },
                navigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                navigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                snackbarHostState = snackbarHostState
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                navigateToRepositoryDetails = { owner, repo ->
                    navController.navigate(Screen.RepositoryDetails.createRoute(owner, repo))
                },
                navigateBack = {
                    navController.popBackStack()
                },
                snackbarHostState = snackbarHostState
            )
        }

        composable(
            route = Screen.RepositoryDetails.route,
            arguments = listOf(
                navArgument("owner") { type = NavType.StringType },
                navArgument("repo") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val owner = backStackEntry.arguments?.getString("owner") ?: ""
            val repo = backStackEntry.arguments?.getString("repo") ?: ""

            RepositoryDetailsScreen(
                owner = owner,
                repo = repo,
                navigateToCreateIssue = { repoOwner, repoName ->
                    navController.navigate(Screen.CreateIssue.createRoute(repoOwner, repoName))
                },
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                navigateToProfile = {
                    navController.popBackStack()
                    navController.navigate(Screen.Profile.route)
                },
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                navigateToRepositoryDetails = { owner, repo ->
                    navController.navigate(Screen.RepositoryDetails.createRoute(owner, repo))
                },
                navigateToLogin = {
                    navController.popBackStack()
                    navController.navigate(Screen.Login.route)
                },
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.CreateIssue.route,
            arguments = listOf(
                navArgument("owner") { type = NavType.StringType },
                navArgument("repo") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val owner = backStackEntry.arguments?.getString("owner") ?: ""
            val repo = backStackEntry.arguments?.getString("repo") ?: ""

            CreateIssueScreen(
                owner = owner,
                repo = repo,
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object RepositoryDetails : Screen("repository/{owner}/{repo}") {
        fun createRoute(owner: String, repo: String) = "repository/$owner/$repo"
    }
    data object Login : Screen("login")
    data object Profile : Screen("profile")
    data object CreateIssue : Screen("issue/{owner}/{repo}") {
        fun createRoute(owner: String, repo: String) = "issue/$owner/$repo"
    }
}