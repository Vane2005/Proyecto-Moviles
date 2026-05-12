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
import com.example.cinelog.ui.changePassword.ChangePasswordScreen
import com.google.firebase.auth.FirebaseAuth
import java.net.URLDecoder
import java.net.URLEncoder

private const val ROUTE_LOGIN_BASE = "login"
private const val ROUTE_LOGIN_FULL = "login?successMessage={successMessage}"
private const val ROUTE_REGISTER = "register"
private const val ROUTE_HOME = "home"
private const val ROUTE_PROFILE = "profile"
private const val ROUTE_DETAIL = "detail/{movieId}"
private const val ROUTE_EDIT_PROFILE = "edit_profile/{nombre}/{edad}/{email}"

private const val ROUTE_CHANGE_PASSWORD = "change_password"

private val PROTECTED_ROUTES = setOf(ROUTE_HOME, ROUTE_PROFILE, "detail")

@Composable
fun CinelogApp() {
    val auth = remember { FirebaseAuth.getInstance() }
    val navController = rememberNavController()
    
    val density = LocalDensity.current
    val moveOffset = with(density) { 30.dp.roundToPx() }
    val animDuration = 400 
    val easing = FastOutSlowInEasing

    // Lógica de navegación unificada para secciones principales (Bottom Bar)
    val navigateToSection: (String) -> Unit = { route ->
        navController.navigate(route) {
            // Buscamos el inicio real para limpiar el stack y asegurar que el botón funcione siempre
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
                    onNavigateToMovieDetail = { movieId -> navController.navigate("detail/$movieId") }
                )
            }

            composable(
                route = ROUTE_DETAIL,
                arguments = listOf(navArgument("movieId") { type = NavType.IntType })
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
                MovieDetailScreen(
                    movieId = movieId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToHome = { 
                        // SOLUCIÓN: Regreso directo al Home limpiando el stack del detalle
                        navController.navigate(ROUTE_HOME) {
                            popUpTo(ROUTE_HOME) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToProfile = { navigateToSection(ROUTE_PROFILE) }
                )
            }

            composable(route = ROUTE_PROFILE) {
                ProfileScreen(
                    onNavigateToHome = { navigateToSection(ROUTE_HOME) },
                    onNavigateToProfile = { },
                    onNavigateToEditProfile = { n, ed, em ->
                        navController.navigate("edit_profile/${URLEncoder.encode(n, "UTF-8")}/${URLEncoder.encode(ed, "UTF-8")}/${URLEncoder.encode(em, "UTF-8")}")
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
        }
    }
}
