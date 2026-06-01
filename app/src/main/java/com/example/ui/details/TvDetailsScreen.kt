package com.example.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.di.AppContainer
import com.example.domain.model.Episode
import com.example.domain.model.TvShow

@Composable
fun TvDetailsScreen(
    tvId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToDetails: (Int) -> Unit
) {
    val viewModel: TvDetailsViewModel = viewModel(
        factory = TvDetailsViewModel.Factory(tvId, AppContainer.tvRepository)
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
            val tvShow = state.tvShow
            if (tvShow != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                            AsyncImage(
                                model = tvShow.backdropUrl ?: tvShow.posterUrl,
                                contentDescription = tvShow.title,
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
                                    text = tvShow.title,
                                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = tvShow.firstAirDate.take(4), color = Color.LightGray)
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(text = "${tvShow.numberOfSeasons} Seasons", color = Color.LightGray)
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(text = "⭐ ${tvShow.rating}", color = Color.LightGray)
                                }
                            }
                        }
                    }

                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Overview", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = tvShow.overview, color = Color.LightGray, lineHeight = 24.sp)
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            if (tvShow.cast.isNotEmpty()) {
                                Text(text = "Cast", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    items(tvShow.cast.take(20), key = { it.id }) { castMember ->
                                        CastItem(castMember)
                                    }
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                            }

                            if (tvShow.similarShows.isNotEmpty()) {
                                Text(text = "Related Shows", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    items(tvShow.similarShows.take(20), key = { "similar_${it.id}" }) { similarShow ->
                                        SimilarTvShowItem(similarShow, onNavigateToDetails)
                                    }
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }

                    // Seasons selector
                    if (tvShow.seasons.isNotEmpty()) {
                        item {
                            ScrollableTabRow(
                                selectedTabIndex = tvShow.seasons.indexOfFirst { it.seasonNumber == state.selectedSeason }.coerceAtLeast(0),
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.primary,
                                edgePadding = 16.dp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            ) {
                                tvShow.seasons.forEachIndexed { index, season ->
                                    val isSelected = season.seasonNumber == state.selectedSeason
                                    Tab(
                                        selected = isSelected,
                                        onClick = { viewModel.loadSeason(season.seasonNumber) },
                                        text = { 
                                            Text(
                                                text = season.name, 
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            ) 
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Episodes loading state
                    if (state.isEpisodesLoading) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    } else if (state.episodesError != null) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text(text = state.episodesError ?: "Error loading episodes", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    } else if (state.episodes.isNotEmpty()) {
                        items(state.episodes, key = { "ep_${it.id}" }) { episode ->
                            EpisodeItem(episode)
                        }
                    }
                }
            }
        }

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
fun EpisodeItem(episode: Episode) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Future: Navigate to Player */ }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .width(130.dp)
                .height(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            AsyncImage(
                model = episode.stillUrl,
                contentDescription = episode.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Play icon overlay could be added here
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${episode.episodeNumber}. ${episode.name}",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = episode.airDate.takeIf { it.isNotEmpty() } ?: "Unknown", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                if (episode.runtime > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "${episode.runtime}m", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = episode.overview,
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
@Composable
fun SimilarTvShowItem(tvShow: TvShow, onClick: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick(tvShow.id) }
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
