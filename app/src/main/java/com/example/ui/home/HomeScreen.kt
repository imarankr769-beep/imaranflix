package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.di.AppContainer
import com.example.domain.model.Movie
import com.example.domain.model.TvShow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,
    onTvShowClick: (Int) -> Unit,
    onNavigateToSearch: () -> Unit
) {
    val factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(AppContainer.movieRepository, AppContainer.tvRepository) as T
        }
    }
    val viewModel: HomeViewModel = viewModel(factory = factory)
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ImaranFlix", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (state.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.error ?: "Unknown error", color = MaterialTheme.colorScheme.error)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 32.dp)
                ) {
                    HeroHeader(movie = state.popularMovies.firstOrNull(), onMovieClick = onMovieClick)
                    
                    MovieRow("Popular Movies", state.popularMovies, onMovieClick)
                    TvRow("Trending TV", state.trendingTv, onTvShowClick)
                    MovieRow("Top Rated Movies", state.topRatedMovies, onMovieClick)
                    TvRow("Popular TV", state.popularTv, onTvShowClick)
                    MovieRow("Upcoming Movies", state.upcomingMovies, onMovieClick)
                    TvRow("Top Rated TV", state.topRatedTv, onTvShowClick)
                }
            }
        }
    }
}

@Composable
fun HeroHeader(movie: Movie?, onMovieClick: (Int) -> Unit) {
    if (movie == null) return
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .clickable { onMovieClick(movie.id) }
    ) {
        AsyncImage(
            model = movie.posterUrl ?: movie.backdropUrl,
            contentDescription = movie.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = movie.releaseDate.take(4),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun MovieRow(title: String, movies: List<Movie>, onMovieClick: (Int) -> Unit) {
    Column(modifier = Modifier.padding(top = 24.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(movies, key = { it.id }) { movie ->
                MovieCard(movie = movie, onClick = { onMovieClick(movie.id) })
            }
        }
    }
}

@Composable
fun MovieCard(movie: Movie, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
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

@Composable
fun TvRow(title: String, tvShows: List<TvShow>, onTvShowClick: (Int) -> Unit) {
    Column(modifier = Modifier.padding(top = 24.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tvShows, key = { it.id }) { tvShow ->
                TvCard(tvShow = tvShow, onClick = { onTvShowClick(tvShow.id) })
            }
        }
    }
}

@Composable
fun TvCard(tvShow: TvShow, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        AsyncImage(
            model = tvShow.posterUrl,
            contentDescription = tvShow.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
