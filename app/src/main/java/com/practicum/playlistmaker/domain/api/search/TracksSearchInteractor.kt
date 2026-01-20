package com.practicum.playlistmaker.domain.api.search


import com.practicum.playlistmaker.domain.models.TracksResponse
import kotlinx.coroutines.flow.Flow

interface TracksSearchInteractor {
    fun searchTracks(expression: String): Flow<TracksResponse>
}