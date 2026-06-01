package com.example.data.remote

import com.example.domain.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TMDBApi {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): MovieResponse
    
    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): MovieResponse

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): MovieResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @retrofit2.http.Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("append_to_response") appendToResponse: String = "credits,similar"
    ): MovieDetailDto

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): MovieResponse

    @GET("trending/tv/week")
    suspend fun getTrendingTv(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): com.example.domain.model.TvResponse

    @GET("tv/popular")
    suspend fun getPopularTv(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): com.example.domain.model.TvResponse
    
    @GET("tv/top_rated")
    suspend fun getTopRatedTv(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): com.example.domain.model.TvResponse

    @GET("tv/{tv_id}")
    suspend fun getTvDetails(
        @retrofit2.http.Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String,
        @Query("append_to_response") appendToResponse: String = "credits,similar"
    ): com.example.domain.model.TvDetailDto

    @GET("tv/{tv_id}/season/{season_number}")
    suspend fun getTvSeason(
        @retrofit2.http.Path("tv_id") tvId: Int,
        @retrofit2.http.Path("season_number") seasonNumber: Int,
        @Query("api_key") apiKey: String
    ): com.example.domain.model.SeasonDetailDto

    @GET("search/tv")
    suspend fun searchTvShows(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): com.example.domain.model.TvResponse
}
