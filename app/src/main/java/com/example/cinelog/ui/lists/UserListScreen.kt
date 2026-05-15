package com.example.cinelog.ui.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cinelog.data.model.MovieItem
import com.example.cinelog.domain.model.ListType
import com.example.cinelog.ui.components.CineLogColors
import com.example.cinelog.ui.components.CinelogBottomBar

private const val IMAGE_BASE = "https://image.tmdb.org/t/p/w500"

@Composable
fun UserListScreen(
    viewModel: UserListViewModel = viewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToLists: () -> Unit = {},
    onMovieClick: (Int, String) -> Unit = { _, _ -> }
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) }

    val tabs = listOf(
        "Favoritas",
        "Ver más tarde",
        "Vistas"
    )

    val currentMovies = when (selectedTab) {
        0 -> uiState.favoritas
        1 -> uiState.watchlist
        else -> uiState.yaVisto
    }

    val currentType = when (selectedTab) {
        0 -> ListType.FAVORITAS
        1 -> ListType.WATCHLIST
        else -> ListType.YA_VISTO
    }

    Scaffold(
        containerColor = CineLogColors.Background,
        bottomBar = {
            CinelogBottomBar(
                currentRoute = "lists",
                onNavigateToHome = onNavigateToHome,
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToLists = onNavigateToLists
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(CineLogColors.Background)
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Mis listas",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = CineLogColors.Background,
                contentColor = Color.White
            ) {

                tabs.forEachIndexed { index, title ->

                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    )
                }
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.White
                    )
                }
            } else {
                if (currentMovies.isEmpty()) {
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        EmptyListState(
                            title = tabs[selectedTab]
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(
                            top = 20.dp,
                            bottom = 100.dp
                        )
                    ) {
                        items(currentMovies) { movie ->
                            MovieListCard(
                                movie = movie,
                                onMovieClick = {
                                    onMovieClick(movie.movieId, movie.mediaType)
                                },
                                onRemove = {
                                    viewModel.removeFromList(
                                        movie,
                                        currentType
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MovieListCard(
    movie: MovieItem,
    onMovieClick: () -> Unit,
    onRemove: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onMovieClick()
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = CineLogColors.SearchBar
        )
    ) {

        Column {
            Box {
                AsyncImage(
                    model = IMAGE_BASE + movie.posterPath,
                    contentDescription = movie.titulo,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.8f)
                                )
                            )
                        )
                )

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                Text(
                    text = movie.titulo,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )
                
                // Opcional: Mostrar si es serie o película
                val typeText = if (movie.mediaType == "tv") "Serie" else "Película"
                Text(
                    text = typeText,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun EmptyListState(
    title: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No hay elementos en",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}
