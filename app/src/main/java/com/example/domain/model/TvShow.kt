package com.example.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TvResponse(
    val page: Int,
    val results: List<TvDto>
)

@JsonClass(generateAdapter = true)
data class TvDto(
    val id: Int,
    val name: String?,
    val poster_path: String?,
    val backdrop_path: String?,
    val overview: String?,
    val first_air_date: String?,
    val vote_average: Double?
)

@JsonClass(generateAdapter = true)
data class TvDetailDto(
    val id: Int,
    val name: String?,
    val poster_path: String?,
    val backdrop_path: String?,
    val overview: String?,
    val first_air_date: String?,
    val vote_average: Double?,
    val number_of_seasons: Int?,
    val genres: List<GenreDto>?,
    val credits: CreditsDto?,
    val similar: TvResponse?,
    val seasons: List<SeasonDto>?
)

@JsonClass(generateAdapter = true)
data class SeasonDto(
    val id: Int,
    val name: String?,
    val season_number: Int,
    val episode_count: Int?,
    val poster_path: String?
)

@JsonClass(generateAdapter = true)
data class SeasonDetailDto(
    val id: Int,
    val name: String?,
    val season_number: Int,
    val poster_path: String?,
    val episodes: List<EpisodeDto>?
)

@JsonClass(generateAdapter = true)
data class EpisodeDto(
    val id: Int,
    val name: String?,
    val overview: String?,
    val episode_number: Int,
    val runtime: Int?,
    val still_path: String?,
    val air_date: String?
)

data class Season(
    val id: Int,
    val name: String,
    val seasonNumber: Int,
    val episodeCount: Int,
    val posterUrl: String?
)

data class Episode(
    val id: Int,
    val name: String,
    val overview: String,
    val episodeNumber: Int,
    val runtime: Int,
    val stillUrl: String?,
    val airDate: String
)

data class TvShow(
    val id: Int,
    val title: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val overview: String,
    val firstAirDate: String,
    val rating: Double
)

data class TvDetail(
    val id: Int,
    val title: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val overview: String,
    val firstAirDate: String,
    val rating: Double,
    val numberOfSeasons: Int,
    val genres: List<String>,
    val cast: List<Cast>,
    val similarShows: List<TvShow>,
    val seasons: List<Season>
)

fun TvDto.toDomainModel(): TvShow {
    return TvShow(
        id = id,
        title = name ?: "Unknown Title",
        posterUrl = poster_path?.let { "https://image.tmdb.org/t/p/w500$it" },
        backdropUrl = backdrop_path?.let { "https://image.tmdb.org/t/p/w1280$it" },
        overview = overview ?: "",
        firstAirDate = first_air_date ?: "",
        rating = vote_average ?: 0.0
    )
}

fun TvDetailDto.toDomainModel(): TvDetail {
    return TvDetail(
        id = id,
        title = name ?: "Unknown",
        posterUrl = poster_path?.let { "https://image.tmdb.org/t/p/w500$it" },
        backdropUrl = backdrop_path?.let { "https://image.tmdb.org/t/p/w1280$it" },
        overview = overview ?: "",
        firstAirDate = first_air_date ?: "",
        rating = vote_average ?: 0.0,
        numberOfSeasons = number_of_seasons ?: 0,
        genres = genres?.map { it.name } ?: emptyList(),
        cast = credits?.cast?.map { Cast(it.id, it.name, it.profile_path?.let { path -> "https://image.tmdb.org/t/p/w500$path" }) } ?: emptyList(),
        similarShows = similar?.results?.map { it.toDomainModel() } ?: emptyList(),
        seasons = seasons?.map { Season(it.id, it.name ?: "", it.season_number, it.episode_count ?: 0, it.poster_path?.let { p -> "https://image.tmdb.org/t/p/w500$p" }) } ?: emptyList()
    )
}
