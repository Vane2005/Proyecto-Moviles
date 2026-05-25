package com.example.cinelog.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import com.example.cinelog.domain.model.HomeContentTab
import com.example.cinelog.ui.components.OfflineScreen
import com.example.cinelog.ui.components.ErrorScreen
import com.example.cinelog.ui.components.LoadingScreen
import androidx.compose.ui.platform.LocalContext

private const val IMAGE_BASE = "https://image.tmdb.org/t/p/w500"
private const val BACKDROP_BASE = "https://image.tmdb.org/t/p/w780"

@Composable
fun HomeScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToLists: () -> Unit = {},
    onNavigateToDetail: (Int, String) -> Unit = { _, _ -> },
    onNavigateToSearch: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel { HomeViewModel(context = context.applicationContext) }

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
        val errorMessage = uiState.errorMessage

        if (uiState.isLoading) {
            LoadingScreen()
        } else if (uiState.isOffline) {
            OfflineScreen(onRetry = viewModel::retryLoad)
        } else if (errorMessage != null) {
            ErrorScreen(
                message = errorMessage,
                onRetry = viewModel::retryLoad
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                item {
                    SearchBar(onClick = onNavigateToSearch)

                    HomeTabSelector(
                        selectedTab = uiState.selectedTab,
                        onTabSelected = viewModel::selectTab
                    )

                    when (uiState.selectedTab) {

                        HomeContentTab.MOVIES -> {
                            uiState.featuredMovie?.let {
                                FeaturedBanner(it) { onNavigateToDetail(it.id, "movie") }
                            }
                            SectionTitle("Terror")
                            MovieCarousel(uiState.horrorMovies) { id ->
                                onNavigateToDetail(
                                    id,
                                    "movie"
                                )
                            }
                            SectionTitle("Romance")
                            MovieCarousel(uiState.romanceMovies) { id ->
                                onNavigateToDetail(
                                    id,
                                    "movie"
                                )
                            }
                            SectionTitle("Animación")
                            MovieCarousel(uiState.animationMovies) { id ->
                                onNavigateToDetail(
                                    id,
                                    "movie"
                                )
                            }
                        }

                        HomeContentTab.SERIES -> {
                            uiState.featuredSeries?.let {
                                FeaturedBanner(it) { onNavigateToDetail(it.id, "tv") }
                            }
                            SectionTitle("Drama")
                            MovieCarousel(uiState.dramaSeries) { id ->
                                onNavigateToDetail(
                                    id,
                                    "tv"
                                )
                            }
                            SectionTitle("Romance")
                            MovieCarousel(uiState.romanceSeries) { id ->
                                onNavigateToDetail(
                                    id,
                                    "tv"
                                )
                            }
                            SectionTitle("Animación")
                            MovieCarousel(uiState.animationSeries) { id ->
                                onNavigateToDetail(
                                    id,
                                    "tv"
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                }
            }
        }
    }
}

@Composable
fun SearchBar(onClick: () -> Unit) {
    OutlinedTextField(
        value = "",
        onValueChange = { },
        readOnly = true,
        placeholder = { Text("Buscar película...", color = CineLogColors.SearchText) },
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
            disabledBorderColor     = CineLogColors.SearchBar,
            disabledPlaceholderColor = CineLogColors.SearchText,
            disabledLeadingIconColor = CineLogColors.SearchText,
            disabledContainerColor = CineLogColors.SearchBar
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        enabled = false
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
fun MovieCarousel(movies: List<Movie>, onItemClick: (Int) -> Unit) {
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
            MovieCard(movie, cardWidth, onClick = { onItemClick(movie.id) })
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

@Composable
fun HomeTabSelector(
    selectedTab: HomeContentTab,
    onTabSelected: (HomeContentTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FilterChip(
            selected = selectedTab == HomeContentTab.MOVIES,
            onClick = { onTabSelected(HomeContentTab.MOVIES) },
            label = { Text("Películas") },
            modifier = Modifier.weight(1f)
        )
        FilterChip(
            selected = selectedTab == HomeContentTab.SERIES,
            onClick = { onTabSelected(HomeContentTab.SERIES) },
            label = { Text("Series") },
            modifier = Modifier.weight(1f)
        )
    }
}
