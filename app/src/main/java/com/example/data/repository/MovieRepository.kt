package com.example.data.repository

import com.example.data.remote.TMDBApi
import com.example.domain.model.Movie
import com.example.domain.model.toDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieRepository(private val api: TMDBApi, private val apiKey: String) {
    suspend fun getPopularMovies(): Result<List<Movie>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getPopularMovies(apiKey)
            Result.success(response.results.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTopRatedMovies(): Result<List<Movie>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getTopRatedMovies(apiKey)
            Result.success(response.results.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUpcomingMovies(): Result<List<Movie>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getUpcomingMovies(apiKey)
            Result.success(response.results.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMovieDetails(movieId: Int): Result<com.example.domain.model.MovieDetail> = withContext(Dispatchers.IO) {
        try {
            val response = api.getMovieDetails(movieId = movieId, apiKey = apiKey)
            Result.success(response.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchMovies(query: String): Result<List<Movie>> = withContext(Dispatchers.IO) {
        try {
            val response = api.searchMovies(apiKey = apiKey, query = query)
            Result.success(response.results.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
