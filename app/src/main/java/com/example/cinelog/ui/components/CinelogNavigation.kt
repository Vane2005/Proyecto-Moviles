package com.example.cinelog.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Sistema de colores centralizado y profesional
object CineLogColors {
    val Background      = Color(0xFF0F172A)
    val Surface         = Color(0xFFE2E8F0) // Color para tarjetas y superficies claras
    val SearchBar       = Color(0xFF1E293B) // Color para barras y campos oscuros
    val SearchText      = Color(0xFF94A3B8)
    val SectionTitle    = Color(0xFFF1F5F9)
    val NavBar          = Color(0xFF1E293B)
    val NavIconActive   = Color(0xFF60A5FA)
    val NavIconInactive = Color(0xFF475569)
    val InputBackground = Color(0xFFFFFFFF)
    val Success         = Color(0xFF10B981)
    val Error           = Color(0xFFB91C1C) // Color estándar para errores y logout
}

@Composable
fun CinelogBottomBar(
    currentRoute: String,
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    NavigationBar(
        containerColor = CineLogColors.NavBar
    ) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = onNavigateToHome,
            icon = { Icon(Icons.Default.Home, null) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor   = CineLogColors.NavIconActive,
                unselectedIconColor = CineLogColors.NavIconInactive,
                indicatorColor      = CineLogColors.NavBar,
            )
        )

        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.AutoMirrored.Filled.List, null) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor   = CineLogColors.NavIconActive,
                unselectedIconColor = CineLogColors.NavIconInactive,
                indicatorColor      = CineLogColors.NavBar,
            )
        )

        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = onNavigateToProfile,
            icon = { Icon(Icons.Default.Person, null) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor   = CineLogColors.NavIconActive,
                unselectedIconColor = CineLogColors.NavIconInactive,
                indicatorColor      = CineLogColors.NavBar,
            )
        )
    }
}
