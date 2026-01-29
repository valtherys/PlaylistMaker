package com.practicum.playlistmaker.data.search

import com.practicum.playlistmaker.data.mappers.TrackDtoMapper
import com.practicum.playlistmaker.data.db.AppDatabase
import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.data.network.HttpStatusCodes
import com.practicum.playlistmaker.domain.api.search.TracksSearchRepository
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.models.TracksResponse
import com.practicum.playlistmaker.ui.search.view_model.ResultType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksSearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val appDatabase: AppDatabase,
    private val trackDtoMapper: TrackDtoMapper,
) :
    TracksSearchRepository {
    override fun searchTracks(expression: String): Flow<TracksResponse> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        when (response.resultCode) {
            in HttpStatusCodes.SUCCESS_MIN..HttpStatusCodes.SUCCESS_MAX -> {
                val favoriteTrackIds = appDatabase.trackDao().getTrackIds()
                val tracksRow = (response as TracksSearchResponse).results
                if (tracksRow.isEmpty()) {
                    emit(TracksResponse(response.resultCode, listOf(), ResultType.EMPTY))
                } else {
                    val tracks = convertFromTrackDto(tracksRow, favoriteTrackIds)
                    emit(TracksResponse(response.resultCode, tracks, ResultType.SUCCESS))
                }
            }

            HttpStatusCodes.CONNECTION_ERROR -> emit(
                TracksResponse(
                    HttpStatusCodes.CONNECTION_ERROR,
                    emptyList(),
                    ResultType.CONNECTION
                )
            )

            else -> emit(TracksResponse(response.resultCode, listOf(), ResultType.ERROR))
        }
    }

    private fun convertFromTrackDto(
        tracks: List<TrackDto>,
        favoriteTrackIds: List<String>
    ): List<Track> {
        return tracks.map { track ->
            trackDtoMapper.map(track)
                .apply { isFavorite = favoriteTrackIds.contains(track.trackId) }
        }
    }
}