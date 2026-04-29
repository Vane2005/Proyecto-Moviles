package com.example.cinelog.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cinelog.data.model.Movie

private const val IMAGE_BASE = "https://image.tmdb.org/t/p/w500"
private const val BACKDROP_BASE = "https://image.tmdb.org/t/p/w780"


object CineLogColors {
    val Background      = Color(0xFF0F172A)
    val SearchBar       = Color(0xFF1E293B)
    val SearchText      = Color(0xFF94A3B8)
    val SectionTitle    = Color(0xFFF1F5F9)
    val NavBar          = Color(0xFF1E293B)
    val NavIconActive   = Color(0xFF60A5FA)
    val NavIconInactive = Color(0xFF475569)
}


@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = CineLogColors.Background,
        contentWindowInsets = WindowInsets.safeDrawing, //para que respete la zona segura
        bottomBar = { BottomBar() }
    ) { padding ->


        println("Horror size: ${uiState.horrorMovies.size}")
        println("Romance size: ${uiState.romanceMovies.size}")
        println("Animation size: ${uiState.animationMovies.size}")

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            item {

                SearchBar()

                uiState.featuredMovie?.let {
                    FeaturedBanner(it)
                }


                SectionTitle("Terror")
                MovieCarousel(uiState.horrorMovies)

                SectionTitle("Romance")
                MovieCarousel(uiState.romanceMovies)

                SectionTitle("Animación")
                MovieCarousel(uiState.animationMovies)

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        placeholder = { Text("Buscar...", color = CineLogColors.SearchText) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = CineLogColors.SearchText)
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = CineLogColors.SearchBar,
            focusedContainerColor   = CineLogColors.SearchBar,
            unfocusedTextColor      = CineLogColors.SearchText,
            focusedTextColor        = CineLogColors.SearchText,
            unfocusedBorderColor    = CineLogColors.SearchBar,
            focusedBorderColor      = CineLogColors.NavIconActive,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun FeaturedBanner(movie: Movie) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp)

    ) {
        AsyncImage(
            model = BACKDROP_BASE + (movie.backdropPath ?: ""),
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        color = CineLogColors.SectionTitle,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp)
    )
}

@Composable
fun MovieCarousel(movies: List<Movie>) {


    if (movies.isEmpty()) {
        Text(
            text = "No hay películas",
            color = CineLogColors.SearchText,
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = screenWidth / 3.2f

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(movies) { movie ->
            MovieCard(movie, cardWidth)
        }
    }
}

@Composable
fun MovieCard(movie: Movie, width: Dp) {
    Card(
        modifier = Modifier
            .width(width)
            .height(width * 1.5f),
        shape = RoundedCornerShape(12.dp)

    ) {
        AsyncImage(
            model = IMAGE_BASE + (movie.posterPath ?: ""),
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun BottomBar() {
    NavigationBar(
        containerColor = CineLogColors.NavBar
    ) {

        NavigationBarItem(
            selected = true,
            onClick = {},
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
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Default.Person, null) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor   = CineLogColors.NavIconActive,
                unselectedIconColor = CineLogColors.NavIconInactive,
                indicatorColor      = CineLogColors.NavBar,
            )
        )
    }
}