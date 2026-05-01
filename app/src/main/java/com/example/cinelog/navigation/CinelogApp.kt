package com.example.cinelog.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cinelog.ui.home.HomeScreen
import com.example.cinelog.ui.login.LoginScreen
import com.example.cinelog.ui.register.RegisterScreen
import com.google.firebase.auth.FirebaseAuth

// Constantes de navegación para evitar errores de escritura
private const val ROUTE_LOGIN = "login"
private const val ROUTE_REGISTER = "register"
private const val ROUTE_HOME = "home"

@Composable
fun CinelogApp() {
    val auth = remember { FirebaseAuth.getInstance() }
    var isLoggedIn by remember { mutableStateOf(auth.currentUser != null) }

    // Escucha cambios en el estado de autenticación de Firebase
    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            isLoggedIn = firebaseAuth.currentUser != null
        }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }

    val navController = rememberNavController()

    // Redirección lógica basada en el estado de la sesión
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate(ROUTE_HOME) {
                popUpTo(0) { inclusive = true }
            }
        } else {
            // Solo redirigir a login si no estamos ya en el flujo de registro
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            if (currentRoute != ROUTE_REGISTER) {
                navController.navigate(ROUTE_LOGIN) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) ROUTE_HOME else ROUTE_LOGIN
    ) {
        composable(ROUTE_LOGIN) {
            LoginScreen(
                onNavigateToRegister = { 
                    navController.navigate(ROUTE_REGISTER) 
                }
            )
        }
        
        composable(ROUTE_REGISTER) {
            RegisterScreen(
                onNavigateToLogin = { 
                    navController.popBackStack() 
                }
            )
        }

        composable(ROUTE_HOME) {
            HomeScreen()
        }
    }
}
