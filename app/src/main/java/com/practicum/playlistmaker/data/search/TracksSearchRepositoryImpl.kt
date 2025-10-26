package com.practicum.playlistmaker.data.search

import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.data.network.HttpStatusCodes
import com.practicum.playlistmaker.domain.api.search.TracksSearchRepository
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.models.TracksResponse
import com.practicum.playlistmaker.presentation.player.ResultType

class TracksSearchRepositoryImpl(private val networkClient: NetworkClient) :
    TracksSearchRepository {
    override fun searchTracks(expression: String): TracksResponse {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        return if (response.resultCode.isRequestSuccessful()) {
            val tracksRow = (response as TracksSearchResponse).results
            if (tracksRow.isEmpty()){
                TracksResponse(response.resultCode, listOf(), ResultType.EMPTY)
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
                TracksResponse(response.resultCode, tracks, ResultType.SUCCESS)
            }

        } else {
            TracksResponse(response.resultCode, listOf(), ResultType.ERROR)
        }
    }

    private fun Int.isRequestSuccessful(): Boolean = this in HttpStatusCodes.SUCCESS_MIN..HttpStatusCodes.SUCCESS_MAX
}