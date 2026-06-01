package com.example.di

import com.example.data.remote.TMDBApi
import com.example.data.repository.MovieRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object AppContainer {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    // Add a valid API key locally for testing or specify where to put it
    private const val API_KEY = "f8a9e0486c8f94d07d1bd1acc30ebbe2" // DEMO KEY, please overwrite with build config

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val tmdbApi: TMDBApi by lazy {
        retrofit.create(TMDBApi::class.java)
    }

    val movieRepository: MovieRepository by lazy {
        MovieRepository(tmdbApi, API_KEY)
    }

    val tvRepository: com.example.data.repository.TvRepository by lazy {
        com.example.data.repository.TvRepository(tmdbApi, API_KEY)
    }
}
