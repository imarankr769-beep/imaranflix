package com.example.data.repository

import com.example.data.remote.TMDBApi
import com.example.domain.model.TvDetail
import com.example.domain.model.TvShow
import com.example.domain.model.toDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TvRepository(private val api: TMDBApi, private val apiKey: String) {
    suspend fun getTrendingTv(): Result<List<TvShow>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getTrendingTv(apiKey)
            Result.success(response.results.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPopularTv(): Result<List<TvShow>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getPopularTv(apiKey)
            Result.success(response.results.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTopRatedTv(): Result<List<TvShow>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getTopRatedTv(apiKey)
            Result.success(response.results.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTvDetails(tvId: Int): Result<TvDetail> = withContext(Dispatchers.IO) {
        try {
            val response = api.getTvDetails(tvId = tvId, apiKey = apiKey)
            Result.success(response.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTvSeason(tvId: Int, seasonNumber: Int): Result<List<com.example.domain.model.Episode>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getTvSeason(tvId, seasonNumber, apiKey)
            val episodes = response.episodes?.map { 
                com.example.domain.model.Episode(
                    id = it.id,
                    name = it.name ?: "Unknown",
                    overview = it.overview ?: "",
                    episodeNumber = it.episode_number,
                    runtime = it.runtime ?: 0,
                    stillUrl = it.still_path?.let { p -> "https://image.tmdb.org/t/p/w500$p" },
                    airDate = it.air_date ?: ""
                )
            } ?: emptyList()
            Result.success(episodes)
        } catch(e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchTvShows(query: String): Result<List<TvShow>> = withContext(Dispatchers.IO) {
        try {
            val response = api.searchTvShows(apiKey = apiKey, query = query)
            Result.success(response.results.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
