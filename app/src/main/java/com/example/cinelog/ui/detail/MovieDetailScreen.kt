package com.example.cinelog.ui.detail

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cinelog.domain.model.ListType
import com.example.cinelog.ui.components.CineLogColors
import com.example.cinelog.ui.components.CinelogBottomBar

private const val IMAGE_BASE = "https://image.tmdb.org/t/p/w500"
private const val BACKDROP_BASE = "https://image.tmdb.org/t/p/w780"

@Composable
fun MovieDetailScreen(
    movieId: Int,
    mediaType: String,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToLists: () -> Unit,
    onNavigateToReview: (Int, String, String) -> Unit,
    viewModel: MovieDetailViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(movieId, mediaType) {
        viewModel.loadDetail(movieId, mediaType)
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = CineLogColors.Background,
        bottomBar = {
            CinelogBottomBar(
                currentRoute = "",
                onNavigateToHome = onNavigateToHome,
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToLists = onNavigateToLists
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CineLogColors.NavIconActive)
            }
        } else if (uiState.movie != null || uiState.tvShow != null) {
            
            val trailerKey = uiState.movie?.videos?.results?.find {
                it.site == "YouTube" && (it.type == "Trailer" || it.type == "Teaser")
            }?.key

            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                        AsyncImage(
                            model = BACKDROP_BASE + (uiState.displayBackdrop ?: ""),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        Box(modifier = Modifier.fillMaxSize().background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.7f),
                                    Color.Transparent,
                                    CineLogColors.Background
                                )
                            )
                        ))

                        TextButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Atras", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .offset(y = (-50).dp)
                    ) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Card(
                                modifier = Modifier.width(130.dp).height(190.dp),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(12.dp)
                            ) {
                                AsyncImage(
                                    model = IMAGE_BASE + (uiState.displayPoster ?: ""),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(20.dp))

                            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                                Text(
                                    text = uiState.displayTitle,
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 30.sp,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Año", color = Color.White, fontSize = 16.sp)
                                        Text(
                                            text = uiState.displayDate.take(4).ifEmpty { "N/A" },
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontSize = 14.sp
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        val label = if (mediaType == "movie") "Dirigido por" else "Tipo"
                                        Text(label, color = Color.White, fontSize = 14.sp)
                                        Text(
                                            text = uiState.director ?: "N/A",
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontSize = 12.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (mediaType == "movie") {
                                        Button(
                                            onClick = {
                                                trailerKey?.let {
                                                    uriHandler.openUri("https://www.youtube.com/watch?v=$it")
                                                }
                                            },
                                            enabled = trailerKey != null,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                                            shape = RoundedCornerShape(12.dp),
                                            contentPadding = PaddingValues(horizontal = 16.dp),
                                            modifier = Modifier.height(36.dp)
                                        ) {
                                            Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (trailerKey != null) "Trailer" else "Sin trailer",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "${uiState.movie?.runtime ?: 0} mins",
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontSize = 13.sp
                                        )
                                    } else {
                                        Surface(
                                            color = CineLogColors.NavIconActive.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "Serie de TV",
                                                color = CineLogColors.NavIconActive,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Rating: ${String.format("%.1f", uiState.displayVoteAverage)}",
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text("Sinopsis", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = uiState.displayOverview.ifEmpty { "No hay sinopsis disponible." },
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 15.sp,
                            lineHeight = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(140.dp))
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(bottom = 24.dp, start = 24.dp, end = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        AnimatedActionButton(
                            text = "Reseña",
                            icon = Icons.Default.Star,
                            modifier = Modifier.weight(1f),
                            onClick = { 
                                onNavigateToReview(uiState.displayId, uiState.displayTitle, uiState.displayPoster ?: "")
                            }
                        )

                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.BottomCenter) {
                            androidx.compose.animation.AnimatedVisibility(
                                visible = uiState.showAddOptions,
                                enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn() + scaleIn(initialScale = 0.8f),
                                exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut() + scaleOut(targetScale = 0.8f),
                                modifier = Modifier.padding(bottom = 68.dp).fillMaxWidth().zIndex(1f)
                            ) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(containerColor = CineLogColors.SearchBar),
                                    elevation = CardDefaults.cardElevation(12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        AddOptionItem(
                                            text = "Favoritas",
                                            enabled = !uiState.inFavoritas
                                        ) {
                                            viewModel.addToList(ListType.FAVORITAS)
                                        }
                                        AddOptionItem(
                                            text = "Ver mas tarde",
                                            enabled = !uiState.inWatchlist
                                        ) {
                                            viewModel.addToList(ListType.WATCHLIST)
                                        }
                                        AddOptionItem(
                                            text = "Vistas",
                                            enabled = !uiState.inYaVisto
                                        ) {
                                            viewModel.addToList(ListType.YA_VISTO)
                                        }
                                    }
                                }
                            }

                            AnimatedActionButton(
                                text = if (uiState.showAddOptions) "Cerrar" else "Agregar",
                                icon = if (uiState.showAddOptions) Icons.Default.Close else Icons.Default.Add,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { viewModel.toggleAddOptions() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
        label = "button_scale"
    )

    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp).scale(scale),
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
        shape = RoundedCornerShape(28.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        ),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Icon(icon, null, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun AddOptionItem(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = if (enabled) text else "$text ✓",
            color = if (enabled) Color.White else Color.White.copy(alpha = 0.4f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}
