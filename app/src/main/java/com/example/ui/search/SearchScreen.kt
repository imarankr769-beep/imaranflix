package com.example.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.di.AppContainer
import com.example.domain.model.Movie
import com.example.domain.model.TvShow
import androidx.compose.foundation.lazy.grid.GridItemSpan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onTvShowClick: (Int) -> Unit
) {
    val factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SearchViewModel(AppContainer.movieRepository, AppContainer.tvRepository) as T
        }
    }
    val viewModel: SearchViewModel = viewModel(factory = factory)
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                OutlinedTextField(
                    value = state.query,
                    onValueChange = viewModel::onQueryChange,
                    modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
                    placeholder = { Text("Search movies & TV shows...", color = Color.Gray) },
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedTextColor = Color.White,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    trailingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                    }
                )
            }
        }

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (state.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.error ?: "Error", color = MaterialTheme.colorScheme.error)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (state.movieResults.isNotEmpty()) {
                    item(span = { GridItemSpan(3) }) {
                        Text("Movies", style = MaterialTheme.typography.titleMedium, color = Color.White, modifier = Modifier.padding(vertical = 8.dp))
                    }
                    items(state.movieResults, key = { "movie_${it.id}" }) { movie ->
                        SearchMovieItem(movie = movie, onClick = { onMovieClick(movie.id) })
                    }
                }
                
                if (state.tvResults.isNotEmpty()) {
                    item(span = { GridItemSpan(3) }) {
                        Text("TV Shows", style = MaterialTheme.typography.titleMedium, color = Color.White, modifier = Modifier.padding(vertical = 8.dp))
                    }
                    items(state.tvResults, key = { "tv_${it.id}" }) { tvShow ->
                        SearchTvItem(tvShow = tvShow, onClick = { onTvShowClick(tvShow.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun SearchMovieItem(movie: Movie, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(2f/3f)
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
fun SearchTvItem(tvShow: TvShow, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(2f/3f)
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
