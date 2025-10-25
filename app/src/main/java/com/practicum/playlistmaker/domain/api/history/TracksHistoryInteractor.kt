package com.practicum.playlistmaker.domain.api.history

import com.practicum.playlistmaker.domain.models.Track

interface TracksHistoryInteractor {
    fun readTracksHistory()

    fun getTracksFromHistory(consumer: TracksHistoryConsumer)

    fun saveTrackInHistory(track: Track)

    fun deleteTracksHistory()

    interface TracksHistoryConsumer{
        fun consume(tracks: List<Track>)
    }
}
