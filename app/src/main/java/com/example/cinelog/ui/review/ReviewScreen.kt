package com.example.cinelog.ui.review

import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
fun ReviewScreen(
    movieId: Int,
    mediaType: String,
    titulo: String,
    posterPath: String,
    viewModel: ReviewViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToLists: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(movieId, mediaType) {
        viewModel.loadReviewForMovie(movieId, mediaType)
    }
    
    LaunchedEffect(uiState.isSaveSuccessful) {
        if (uiState.isSaveSuccessful) {
            Toast.makeText(context, uiState.successToastMessage ?: "Reseña guardada con éxito", Toast.LENGTH_SHORT).show()
            onNavigateBack()
        }
    }

    val saveButtonText = if (uiState.hasExistingReview) "Actualizar" else "Guardar"

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
        if (uiState.isLoadingReview) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = CineLogColors.NavIconActive)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Header: Botón Atrás
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateBack() }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = CineLogColors.SectionTitle
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Atras",
                        color = CineLogColors.SectionTitle,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Info de la película y Calificación
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = IMAGE_BASE + posterPath,
                        contentDescription = titulo,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(width = 100.dp, height = 150.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = titulo,
                            color = CineLogColors.SectionTitle,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        RatingBar(
                            rating = uiState.calificacion,
                            onRatingChanged = { viewModel.onCalificacionChange(it) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Sección Descripción
                Text(
                    text = "Descripción",
                    color = CineLogColors.SectionTitle,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                // Caja de texto con efecto de sombra
                OutlinedTextField(
                    value = uiState.reseña,
                    onValueChange = { viewModel.onReseñaChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .shadow(elevation = 10.dp, shape = RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFF1E293B),
                        focusedContainerColor = Color(0xFF1E293B),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                        focusedBorderColor = CineLogColors.NavIconActive,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Sección Etiquetas
                Text(
                    text = "Etiquetas",
                    color = CineLogColors.SectionTitle,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Caja de etiquetas con efecto de sombra
                OutlinedTextField(
                    value = uiState.etiquetas,
                    onValueChange = { viewModel.onEtiquetasChange(it) },
                    placeholder = { Text("Drama, Thriller...", color = CineLogColors.SearchText) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 10.dp, shape = RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFF1E293B),
                        focusedContainerColor = Color(0xFF1E293B),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                        focusedBorderColor = CineLogColors.NavIconActive,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Botones Cancelar y Guardar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { onNavigateBack() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CineLogColors.Error),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Cancelar", fontSize = 18.sp, color = Color.White)
                    }

                    Button(
                        onClick = { viewModel.saveReview(movieId, mediaType, titulo, posterPath) },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CineLogColors.Success),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            //Guardar o actualizar reseña
                            Text(saveButtonText, fontSize = 18.sp, color = Color.White)
                        }
                    }
                }
                
                uiState.errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = CineLogColors.Error,
                        modifier = Modifier.padding(top = 8.dp).align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
fun RatingBar(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = null,
                tint = if (i <= rating) Color.White else CineLogColors.SearchText,
                modifier = Modifier
                    .size(36.dp)
                    .clickable { onRatingChanged(i) }
            )
        }
    }
}
