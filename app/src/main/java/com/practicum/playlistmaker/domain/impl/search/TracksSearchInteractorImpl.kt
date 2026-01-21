package com.practicum.playlistmaker.domain.impl.search

import com.practicum.playlistmaker.domain.api.search.TracksSearchInteractor
import com.practicum.playlistmaker.domain.api.search.TracksSearchRepository
import com.practicum.playlistmaker.domain.models.TracksResponse
import kotlinx.coroutines.flow.Flow

class TracksSearchInteractorImpl(private val repository: TracksSearchRepository) :
    TracksSearchInteractor {

    override fun searchTracks(
        expression: String
    ): Flow<TracksResponse> = repository.searchTracks(expression)
}