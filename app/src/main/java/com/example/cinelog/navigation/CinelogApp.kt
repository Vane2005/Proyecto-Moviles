package com.example.cinelog.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cinelog.ui.components.CineLogColors
import com.example.cinelog.ui.home.HomeScreen
import com.example.cinelog.ui.login.LoginScreen
import com.example.cinelog.ui.profile.ProfileScreen
import com.example.cinelog.ui.register.RegisterScreen
import com.example.cinelog.ui.detail.MovieDetailScreen
import com.example.cinelog.ui.editProfile.EditProfileScreen
import com.example.cinelog.ui.lists.UserListScreen
import com.example.cinelog.ui.changePassword.ChangePasswordScreen
import com.example.cinelog.ui.review.ReviewScreen
import com.example.cinelog.ui.review.UserReviewsScreen
import com.example.cinelog.ui.search.SearchScreen
import com.google.firebase.auth.FirebaseAuth
import java.net.URLDecoder
import java.net.URLEncoder

private const val ROUTE_LOGIN_BASE = "login"
private const val ROUTE_LOGIN_FULL = "login?successMessage={successMessage}"
private const val ROUTE_REGISTER = "register"
private const val ROUTE_HOME = "home"
private const val ROUTE_SEARCH = "search"
private const val ROUTE_PROFILE = "profile"
private const val ROUTE_DETAIL = "detail/{movieId}/{mediaType}"
private const val ROUTE_EDIT_PROFILE = "edit_profile/{nombre}/{edad}/{email}"
private const val ROUTE_CHANGE_PASSWORD = "change_password"
private const val ROUTE_USER_LISTS = "user_lists"
private const val ROUTE_REVIEW = "review/{movieId}/{mediaType}/{titulo}/{posterPath}"
private const val ROUTE_USER_REVIEWS = "user_reviews"

private val PROTECTED_ROUTES = setOf(ROUTE_HOME, ROUTE_SEARCH, ROUTE_PROFILE, "detail", "review", ROUTE_USER_REVIEWS)

@Composable
fun CinelogApp() {
    val auth = remember { FirebaseAuth.getInstance() }
    val navController = rememberNavController()
    
    val density = LocalDensity.current
    val moveOffset = with(density) { 30.dp.roundToPx() }
    val animDuration = 400 
    val easing = FastOutSlowInEasing

    val navigateToSection: (String) -> Unit = { route ->
        navController.navigate(route) {
            val startId = navController.graph.findStartDestination().id
            popUpTo(startId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    DisposableEffect(navController) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser == null) {
                val currentRoute = navController.currentDestination?.route
                if (currentRoute != null && PROTECTED_ROUTES.any { currentRoute.startsWith(it) }) {
                    navController.navigate(ROUTE_LOGIN_BASE) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }

    val startDestination = remember {
        if (auth.currentUser != null) ROUTE_HOME else ROUTE_LOGIN_BASE
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CineLogColors.Background
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize(),
            enterTransition = { fadeIn(tween(animDuration)) + slideInHorizontally(tween(animDuration, easing = easing)) { moveOffset } },
            exitTransition = { fadeOut(tween(animDuration)) + slideOutHorizontally(tween(animDuration, easing = easing)) { -moveOffset } },
            popEnterTransition = { fadeIn(tween(animDuration)) + slideInHorizontally(tween(animDuration, easing = easing)) { -moveOffset } },
            popExitTransition = { fadeOut(tween(animDuration)) + slideOutHorizontally(tween(animDuration, easing = easing)) { moveOffset } }
        ) {
            composable(
                route = ROUTE_LOGIN_FULL,
                arguments = listOf(navArgument("successMessage") { type = NavType.StringType; nullable = true; defaultValue = null })
            ) { backStackEntry ->
                val encodedMessage = backStackEntry.arguments?.getString("successMessage")
                val successMessage = encodedMessage?.let { URLDecoder.decode(it, "UTF-8") }
                LoginScreen(
                    onNavigateToRegister = { navController.navigate(ROUTE_REGISTER) },
                    onNavigateToHome = { 
                        navController.navigate(ROUTE_HOME) { 
                            popUpTo(0) { inclusive = true } 
                        } 
                    },
                    registrationSuccessMessage = successMessage
                )
            }
            
            composable(route = ROUTE_REGISTER) {
                RegisterScreen(
                    onNavigateToLogin = { message ->
                        if (message != null) {
                            val encoded = URLEncoder.encode(message, "UTF-8")
                            navController.navigate("login?successMessage=$encoded") { popUpTo(0) { inclusive = true } }
                        } else {
                            navController.popBackStack()
                        }
                    }
                )
            }

            composable(route = ROUTE_HOME) {
                HomeScreen(
                    onNavigateToHome = { },
                    onNavigateToProfile = { navigateToSection(ROUTE_PROFILE) },
                    onNavigateToMovieDetail = { movieId -> navController.navigate("detail/$movieId/movie") },
                    onNavigateToLists = { navigateToSection(ROUTE_USER_LISTS) },
                    onNavigateToSearch = { navController.navigate(ROUTE_SEARCH) }
                )
            }

            composable(route = ROUTE_SEARCH) {
                SearchScreen(
                    onNavigateToHome = {
                        navController.navigate(ROUTE_HOME) {
                            popUpTo(ROUTE_HOME) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToProfile = { navigateToSection(ROUTE_PROFILE) },
                    onNavigateToLists = { navigateToSection(ROUTE_USER_LISTS) },
                    onNavigateToMovieDetail = { movieId, mediaType -> navController.navigate("detail/$movieId/$mediaType") }
                )
            }

            composable(
                route = ROUTE_DETAIL,
                arguments = listOf(
                    navArgument("movieId") { type = NavType.IntType },
                    navArgument("mediaType") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
                val mediaType = backStackEntry.arguments?.getString("mediaType") ?: "movie"
                MovieDetailScreen(
                    movieId = movieId,
                    mediaType = mediaType,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToHome = {
                        navController.navigate(ROUTE_HOME) {
                            popUpTo(ROUTE_HOME) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToProfile = { navigateToSection(ROUTE_PROFILE) },
                    onNavigateToLists = { navigateToSection(ROUTE_USER_LISTS) },
                    onNavigateToReview = { id: Int, title: String, poster: String ->
                        val encodedTitle = URLEncoder.encode(title, "UTF-8")
                        val encodedPoster = URLEncoder.encode(poster, "UTF-8")
                        navController.navigate("review/$id/$mediaType/$encodedTitle/$encodedPoster")
                    }
                )
            }

            composable(
                route = ROUTE_REVIEW,
                arguments = listOf(
                    navArgument("movieId") { type = NavType.IntType },
                    navArgument("mediaType") { type = NavType.StringType },
                    navArgument("titulo") { type = NavType.StringType },
                    navArgument("posterPath") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
                val mediaType = backStackEntry.arguments?.getString("mediaType") ?: "movie"
                val titulo = URLDecoder.decode(backStackEntry.arguments?.getString("titulo") ?: "", "UTF-8")
                val posterPath = URLDecoder.decode(backStackEntry.arguments?.getString("posterPath") ?: "", "UTF-8")
                
                ReviewScreen(
                    movieId = movieId,
                    mediaType = mediaType,
                    titulo = titulo,
                    posterPath = posterPath,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToHome = { navigateToSection(ROUTE_HOME) },
                    onNavigateToProfile = { navigateToSection(ROUTE_PROFILE) },
                    onNavigateToLists = { navigateToSection(ROUTE_USER_LISTS) }
                )
            }

            composable(route = ROUTE_PROFILE) {
                ProfileScreen(
                    onNavigateToHome = { navigateToSection(ROUTE_HOME) },
                    onNavigateToProfile = { },
                    onNavigateToLists = { navigateToSection(ROUTE_USER_LISTS) },
                    onNavigateToEditProfile = { n, ed, em ->
                        navController.navigate("edit_profile/${URLEncoder.encode(n, "UTF-8")}/${URLEncoder.encode(ed, "UTF-8")}/${URLEncoder.encode(em, "UTF-8")}")
                    },
                    onNavigateToMyReviews = {
                        navController.navigate(ROUTE_USER_REVIEWS)
                    }
                )
            }

            composable(route = ROUTE_USER_REVIEWS) {
                UserReviewsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToHome = { navigateToSection(ROUTE_HOME) },
                    onNavigateToProfile = { navigateToSection(ROUTE_PROFILE) },
                    onNavigateToLists = { navigateToSection(ROUTE_USER_LISTS) },
                    onReviewClick = { movieId, mediaType ->
                        navController.navigate("detail/$movieId/$mediaType")
                    },
                    onEditReview = { review ->
                        val encodedTitle = URLEncoder.encode(review.titulo, "UTF-8")
                        val encodedPoster = URLEncoder.encode(review.posterPath, "UTF-8")
                        navController.navigate(
                            "review/${review.movieId}/${review.mediaType}/$encodedTitle/$encodedPoster"
                        )
                    }
                )
            }

            composable(
                route = ROUTE_EDIT_PROFILE,
                arguments = listOf(navArgument("nombre") { type = NavType.StringType }, navArgument("edad") { type = NavType.StringType }, navArgument("email") { type = NavType.StringType })
            ) { backStackEntry ->
                EditProfileScreen(
                    initialNombre = URLDecoder.decode(backStackEntry.arguments?.getString("nombre") ?: "", "UTF-8"),
                    initialEdad = URLDecoder.decode(backStackEntry.arguments?.getString("edad") ?: "", "UTF-8"),
                    initialEmail = URLDecoder.decode(backStackEntry.arguments?.getString("email") ?: "", "UTF-8"),
                    onNavigateBack = { navController.popBackStack() },
                    onSaveSuccessful = { navController.popBackStack() },
                    onChangePassword = {
                        navController.navigate(ROUTE_CHANGE_PASSWORD)
                    }
                )
            }

            composable(route = ROUTE_CHANGE_PASSWORD) {
                ChangePasswordScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onPasswordChanged = {
                        navController.popBackStack()
                    }
                )
            }

            composable(route = ROUTE_USER_LISTS) {
                UserListScreen(
                    onNavigateToHome = { navigateToSection(ROUTE_HOME) },
                    onNavigateToProfile = { navigateToSection(ROUTE_PROFILE) },
                    onNavigateToLists = {},
                    onMovieClick = { movieId, mediaType ->
                        navController.navigate("detail/$movieId/$mediaType")
                    }
                )
            }
        }
    }
}
