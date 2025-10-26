package com.practicum.playlistmaker.domain.api.search

import com.practicum.playlistmaker.domain.models.TracksResponse

interface TracksSearchRepository {
    fun searchTracks(expression: String): TracksResponse
}