package com.example.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.repository.MovieRepository
import com.example.domain.model.MovieDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MovieDetailsUiState(
    val movie: MovieDetail? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class MovieDetailsViewModel(
    private val movieId: Int,
    private val repository: MovieRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MovieDetailsUiState())
    val uiState: StateFlow<MovieDetailsUiState> = _uiState.asStateFlow()

    init {
        loadMovieDetails()
    }

    private fun loadMovieDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.getMovieDetails(movieId)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(isLoading = false, movie = result.getOrNull())
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, error = "Failed to load details.")
                }
            }
        }
    }

    class Factory(private val movieId: Int, private val repository: MovieRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MovieDetailsViewModel(movieId, repository) as T
        }
    }
}
