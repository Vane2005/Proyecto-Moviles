package com.example.cinelog.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cinelog.data.model.Movie
import com.example.cinelog.ui.components.CineLogColors
import com.example.cinelog.ui.components.CinelogBottomBar

private const val IMAGE_BASE = "https://image.tmdb.org/t/p/w500"
private const val BACKDROP_BASE = "https://image.tmdb.org/t/p/w780"

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToLists: () -> Unit = {},
    onNavigateToMovieDetail: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = CineLogColors.Background,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = { 
            CinelogBottomBar(
                currentRoute = "home",
                onNavigateToHome = onNavigateToHome,
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToLists = onNavigateToLists
            ) 
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                SearchBar()

                uiState.featuredMovie?.let {
                    FeaturedBanner(movie = it, onClick = { onNavigateToMovieDetail(it.id) })
                }

                SectionTitle("Terror")
                MovieCarousel(uiState.horrorMovies, onMovieClick = onNavigateToMovieDetail)

                SectionTitle("Romance")
                MovieCarousel(uiState.romanceMovies, onMovieClick = onNavigateToMovieDetail)

                SectionTitle("Animación")
                MovieCarousel(uiState.animationMovies, onMovieClick = onNavigateToMovieDetail)

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SearchBar() {
    var query by remember { mutableStateOf("") }
    
    OutlinedTextField(
        value = query,
        onValueChange = { query = it },
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
        shape = RoundedCornerShape(20.dp),
        singleLine = true
    )
}

@Composable
fun FeaturedBanner(movie: Movie, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
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
fun MovieCarousel(movies: List<Movie>, onMovieClick: (Int) -> Unit) {
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
            MovieCard(movie, cardWidth, onClick = { onMovieClick(movie.id) })
        }
    }
}

@Composable
fun MovieCard(movie: Movie, width: Dp, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(width)
            .height(width * 1.5f)
            .clickable { onClick() },
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
