package com.example.cinelog.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.cinelog.data.model.Movie
import com.example.cinelog.ui.components.CineLogColors
import com.example.cinelog.ui.components.CinelogBottomBar

private const val IMAGE_BASE = "https://image.tmdb.org/t/p/w200"

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel(),
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
                currentRoute = "search",
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
        ) {
            // Se coloca aquí para que use el mismo padding del Scaffold que el HomeScreen
            SearchHeader(
                query = uiState.query,
                onQueryChange = viewModel::onQueryChange
            )

            if (uiState.query.isEmpty()) {
                EmptySearchState(
                    icon = Icons.Default.Movie,
                    title = "Busca tus películas favoritas",
                    subtitle = "Explora, reseña y organiza tu colección personal de películas en un solo lugar."
                )
            } else if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CineLogColors.NavIconActive)
                }
            } else if (uiState.results.isEmpty()) {
                EmptySearchState(
                    icon = Icons.Default.Search,
                    title = "Sin resultados",
                    subtitle = "No pudimos encontrar películas que coincidan con \"${uiState.query}\""
                )
            } else {
                Text(
                    text = "RESULTADOS PARA \"${uiState.query.uppercase()}\"",
                    color = CineLogColors.SearchText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
                HorizontalDivider(color = CineLogColors.SearchBar, thickness = 0.5.dp)

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(uiState.results) { movie ->
                        MovieSearchItem(
                            movie = movie,
                            onClick = { onNavigateToMovieDetail(movie.id) }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = CineLogColors.SearchBar
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptySearchState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(32.dp),
            color = CineLogColors.SearchBar.copy(alpha = 0.5f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = CineLogColors.NavIconActive
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = title,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = subtitle,
            color = CineLogColors.SearchText,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

@Composable
fun SearchHeader(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Buscar película...", color = CineLogColors.SearchText) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = CineLogColors.SearchText)
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = CineLogColors.SearchBar,
            focusedContainerColor   = CineLogColors.SearchBar,
            unfocusedTextColor      = Color.White,
            focusedTextColor        = Color.White,
            unfocusedBorderColor    = CineLogColors.SearchBar,
            focusedBorderColor      = CineLogColors.NavIconActive,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
fun MovieSearchItem(
    movie: Movie,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        AsyncImage(
            model = IMAGE_BASE + (movie.posterPath ?: ""),
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(70.dp)
                .height(105.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(CineLogColors.SearchBar)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = movie.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (movie.releaseDate.length >= 4) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = movie.releaseDate.take(4),
                        color = CineLogColors.SearchText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = movie.overview,
                color = CineLogColors.SearchText,
                fontSize = 13.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = CineLogColors.SearchBar,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "Ver detalles",
                    color = CineLogColors.SearchText,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
