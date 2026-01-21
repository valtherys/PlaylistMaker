package com.practicum.playlistmaker.data.search

import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.data.network.HttpStatusCodes
import com.practicum.playlistmaker.domain.api.search.TracksSearchRepository
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.models.TracksResponse
import com.practicum.playlistmaker.ui.search.view_model.ResultType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksSearchRepositoryImpl(private val networkClient: NetworkClient) :
    TracksSearchRepository {
    override fun searchTracks(expression: String): Flow<TracksResponse> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        when (response.resultCode) {
            in HttpStatusCodes.SUCCESS_MIN..HttpStatusCodes.SUCCESS_MAX -> {
                val tracksRow = (response as TracksSearchResponse).results
                if (tracksRow.isEmpty()) {
                    emit(TracksResponse(response.resultCode, listOf(), ResultType.EMPTY))
                } else {
                    val tracks = tracksRow.map {
                        Track(
                            trackName = it.trackName,
                            artistName = it.artistName,
                            trackTime = it.trackTime,
                            artworkUrl100 = it.artworkUrl100,
                            trackId = it.trackId,
                            collectionName = it.collectionName,
                            releaseDate = it.releaseDate,
                            primaryGenreName = it.primaryGenreName,
                            country = it.country,
                            previewUrl = it.previewUrl
                        )
                    }
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
}