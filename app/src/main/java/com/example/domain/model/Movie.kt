package com.example.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MovieResponse(
    val page: Int,
    val results: List<MovieDto>
)

@JsonClass(generateAdapter = true)
data class MovieDto(
    val id: Int,
    val title: String?,
    val poster_path: String?,
    val backdrop_path: String?,
    val overview: String?,
    val release_date: String?,
    val vote_average: Double?
)

@JsonClass(generateAdapter = true)
data class MovieDetailDto(
    val id: Int,
    val title: String?,
    val poster_path: String?,
    val backdrop_path: String?,
    val overview: String?,
    val release_date: String?,
    val vote_average: Double?,
    val runtime: Int?,
    val genres: List<GenreDto>?,
    val credits: CreditsDto?,
    val similar: MovieResponse?
)

@JsonClass(generateAdapter = true)
data class GenreDto(
    val id: Int,
    val name: String
)

@JsonClass(generateAdapter = true)
data class CreditsDto(
    val cast: List<CastDto>?
)

@JsonClass(generateAdapter = true)
data class CastDto(
    val id: Int,
    val name: String,
    val profile_path: String?
)

data class Cast(
    val id: Int,
    val name: String,
    val profileUrl: String?
)

data class MovieDetail(
    val id: Int,
    val title: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val overview: String,
    val releaseDate: String,
    val rating: Double,
    val runtime: Int,
    val genres: List<String>,
    val cast: List<Cast>,
    val similarMovies: List<Movie>
)

fun MovieDetailDto.toDomainModel(): MovieDetail {
    return MovieDetail(
        id = id,
        title = title ?: "Unknown",
        posterUrl = poster_path?.let { "https://image.tmdb.org/t/p/w500$it" },
        backdropUrl = backdrop_path?.let { "https://image.tmdb.org/t/p/w1280$it" },
        overview = overview ?: "",
        releaseDate = release_date ?: "",
        rating = vote_average ?: 0.0,
        runtime = runtime ?: 0,
        genres = genres?.map { it.name } ?: emptyList(),
        cast = credits?.cast?.map { Cast(it.id, it.name, it.profile_path?.let { path -> "https://image.tmdb.org/t/p/w500$path" }) } ?: emptyList(),
        similarMovies = similar?.results?.map { it.toDomainModel() } ?: emptyList()
    )
}

data class Movie(
    val id: Int,
    val title: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val overview: String,
    val releaseDate: String,
    val rating: Double
)

fun MovieDto.toDomainModel(): Movie {
    return Movie(
        id = id,
        title = title ?: "Unknown Title",
        posterUrl = poster_path?.let { "https://image.tmdb.org/t/p/w500$it" },
        backdropUrl = backdrop_path?.let { "https://image.tmdb.org/t/p/w1280$it" },
        overview = overview ?: "",
        releaseDate = release_date ?: "",
        rating = vote_average ?: 0.0
    )
}
