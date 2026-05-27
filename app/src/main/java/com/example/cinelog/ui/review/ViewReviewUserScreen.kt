package com.example.cinelog.ui.review



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cinelog.ui.components.CineLogColors
import com.example.cinelog.ui.components.CinelogBottomBar

private const val IMAGE_BASE = "https://image.tmdb.org/t/p/w500"

@Composable
fun ViewReviewUserScreen(
    movieId: Int,
    mediaType: String,
    viewModel: ViewReviewUserViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToLists: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadReview(movieId, mediaType)
    }

    Scaffold(
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

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator(
                    color = CineLogColors.NavIconActive
                )
            }

        } else {

            uiState.review?.let { review ->

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {

                    // BACK
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        IconButton(onClick = onNavigateBack) {

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }

                        Text(
                            text = "Mi reseña",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // POSTER + INFO
                    Row {

                        AsyncImage(
                            model = IMAGE_BASE + review.posterPath,
                            contentDescription = review.titulo,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(width = 120.dp, height = 180.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = review.titulo,
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row {

                                for (i in 1..5) {

                                    Icon(
                                        imageVector = if (i <= review.calificacion)
                                            Icons.Filled.Star
                                        else
                                            Icons.Outlined.StarBorder,

                                        contentDescription = null,

                                        tint = if (i <= review.calificacion)
                                            Color(0xFFFFD700)
                                        else
                                            CineLogColors.SearchText,

                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = if (review.mediaType == "tv")
                                    "Serie"
                                else
                                    "Película",

                                color = CineLogColors.SearchText,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // ETIQUETAS
                    Text(
                        text = "Etiquetas",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1E293B))
                            .padding(16.dp)
                    ) {

                        Text(
                            text = review.etiquetas.ifBlank { "Sin etiquetas" },
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // RESEÑA
                    Text(
                        text = "Descripción",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1E293B))
                            .padding(18.dp)
                    ) {

                        Text(
                            text = review.reseña,
                            color = Color.White,
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}