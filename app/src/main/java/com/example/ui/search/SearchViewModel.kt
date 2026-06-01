package com.example.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.MovieRepository
import com.example.data.repository.TvRepository
import com.example.domain.model.Movie
import com.example.domain.model.TvShow
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val movieResults: List<Movie> = emptyList(),
    val tvResults: List<TvShow> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class SearchViewModel(
    private val movieRepository: MovieRepository,
    private val tvRepository: TvRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    
    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        searchJob?.cancel()
        
        if (query.isBlank()) {
            _uiState.update { it.copy(movieResults = emptyList(), tvResults = emptyList(), isLoading = false, error = null) }
            return
        }

        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            delay(500) // debounce
            
            val movieResult = movieRepository.searchMovies(query)
            val tvResult = tvRepository.searchTvShows(query)
            
            if (movieResult.isSuccess && tvResult.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        movieResults = movieResult.getOrNull() ?: emptyList(),
                        tvResults = tvResult.getOrNull() ?: emptyList()
                    )
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, error = "Failed to search")
                }
            }
        }
    }
}
