package com.example.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.di.AppContainer
import com.example.domain.model.Cast
import com.example.domain.model.Movie

@Composable
fun MovieDetailsScreen(
    movieId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToDetails: (Int) -> Unit
) {
    val viewModel: MovieDetailsViewModel = viewModel(
        factory = MovieDetailsViewModel.Factory(movieId, AppContainer.movieRepository)
    )
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        } else if (state.error != null) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = state.error ?: "Error",
                color = MaterialTheme.colorScheme.error
            )
        } else {
            val movie = state.movie
            if (movie != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 32.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                        AsyncImage(
                            model = movie.backdropUrl ?: movie.posterUrl,
                            contentDescription = movie.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f))
                        )
                        Column(
                            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
                        ) {
                            Text(
                                text = movie.title,
                                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = movie.releaseDate.take(4), color = Color.LightGray)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(text = "${movie.runtime} min", color = Color.LightGray)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(text = "⭐ ${movie.rating}", color = Color.LightGray)
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Overview", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = movie.overview, color = Color.LightGray, lineHeight = 24.sp)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        if (movie.cast.isNotEmpty()) {
                            Text(text = "Cast", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                items(movie.cast.take(20), key = { it.id }) { castMember ->
                                    CastItem(castMember)
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        if (movie.similarMovies.isNotEmpty()) {
                            Text(text = "Related Movies", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                items(movie.similarMovies.take(20), key = { "similar_${it.id}" }) { similarMovie ->
                                    SimilarMovieItem(similarMovie, onNavigateToDetails)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Back Button overlay
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                .align(Alignment.TopStart)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
    }
}

@Composable
fun CastItem(cast: Cast) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(80.dp)) {
        AsyncImage(
            model = cast.profileUrl,
            contentDescription = cast.name,
            modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.DarkGray),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = cast.name,
            color = Color.White,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SimilarMovieItem(movie: Movie, onClick: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick(movie.id) }
            .background(MaterialTheme.colorScheme.surface)
    ) {
        AsyncImage(
            model = movie.posterUrl,
            contentDescription = movie.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
