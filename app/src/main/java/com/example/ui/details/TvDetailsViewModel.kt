package com.example.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.repository.TvRepository
import com.example.domain.model.Episode
import com.example.domain.model.TvDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TvDetailsUiState(
    val tvShow: TvDetail? = null,
    val selectedSeason: Int? = null,
    val episodes: List<Episode> = emptyList(),
    val isEpisodesLoading: Boolean = false,
    val episodesError: String? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class TvDetailsViewModel(
    private val tvId: Int,
    private val repository: TvRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TvDetailsUiState())
    val uiState: StateFlow<TvDetailsUiState> = _uiState.asStateFlow()

    init {
        loadTvDetails()
    }

    private fun loadTvDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.getTvDetails(tvId)
            if (result.isSuccess) {
                val tvShow = result.getOrNull()
                _uiState.update {
                    it.copy(isLoading = false, tvShow = tvShow)
                }
                val defaultSeason = tvShow?.seasons?.firstOrNull { it.seasonNumber > 0 }?.seasonNumber ?: tvShow?.seasons?.firstOrNull()?.seasonNumber
                if (defaultSeason != null) {
                    loadSeason(defaultSeason)
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, error = "Failed to load TV details.")
                }
            }
        }
    }

    fun loadSeason(seasonNumber: Int) {
        if (_uiState.value.selectedSeason == seasonNumber) return
        _uiState.update { it.copy(selectedSeason = seasonNumber, isEpisodesLoading = true, episodesError = null) }
        viewModelScope.launch {
            val result = repository.getTvSeason(tvId, seasonNumber)
            if (result.isSuccess) {
                _uiState.update { it.copy(isEpisodesLoading = false, episodes = result.getOrNull() ?: emptyList()) }
            } else {
                _uiState.update { it.copy(isEpisodesLoading = false, episodesError = "Failed to load episodes") }
            }
        }
    }

    class Factory(private val tvId: Int, private val repository: TvRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TvDetailsViewModel(tvId, repository) as T
        }
    }
}
