package com.practicum.playlistmaker.domain.api.search


import com.practicum.playlistmaker.domain.models.TracksResponse

interface TracksSearchInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(response: TracksResponse)
    }
}