package com.practicum.playlistmaker.domain.api.history

import com.practicum.playlistmaker.domain.models.TracksHistory
import com.practicum.playlistmaker.domain.models.Track

interface TracksHistoryRepository {

    fun readTracksHistory()

    fun getTracksHistory(): TracksHistory

    fun saveTrackInHistory(track: Track)

    fun clearTracksHistory()
}