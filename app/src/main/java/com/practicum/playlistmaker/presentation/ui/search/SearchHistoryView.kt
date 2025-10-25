package com.practicum.playlistmaker.presentation.ui.search

import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryView {
    fun showTracksHistory(receivedTracks: List<Track>)
    fun hideTracksHistory()
}