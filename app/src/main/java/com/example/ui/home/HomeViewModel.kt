package com.example.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.MovieRepository
import com.example.data.repository.TvRepository
import com.example.domain.model.Movie
import com.example.domain.model.TvShow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val popularMovies: List<Movie> = emptyList(),
    val topRatedMovies: List<Movie> = emptyList(),
    val upcomingMovies: List<Movie> = emptyList(),
    val trendingTv: List<TvShow> = emptyList(),
    val popularTv: List<TvShow> = emptyList(),
    val topRatedTv: List<TvShow> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class HomeViewModel(
    private val movieRepository: MovieRepository,
    private val tvRepository: TvRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val popularResult = movieRepository.getPopularMovies()
            val topRatedResult = movieRepository.getTopRatedMovies()
            val upcomingResult = movieRepository.getUpcomingMovies()
            
            val trendingTvResult = tvRepository.getTrendingTv()
            val popularTvResult = tvRepository.getPopularTv()
            val topRatedTvResult = tvRepository.getTopRatedTv()

            if (popularResult.isSuccess && trendingTvResult.isSuccess) {
                _uiState.update {
                    it.copy(
                        popularMovies = popularResult.getOrNull() ?: emptyList(),
                        topRatedMovies = topRatedResult.getOrNull() ?: emptyList(),
                        upcomingMovies = upcomingResult.getOrNull() ?: emptyList(),
                        trendingTv = trendingTvResult.getOrNull() ?: emptyList(),
                        popularTv = popularTvResult.getOrNull() ?: emptyList(),
                        topRatedTv = topRatedTvResult.getOrNull() ?: emptyList(),
                        isLoading = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load content. Please check your connection."
                    )
                }
            }
        }
    }
}
