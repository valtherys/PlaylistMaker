package com.practicum.playlistmaker.ui.playlist.view_model

import com.practicum.playlistmaker.domain.models.Track

sealed interface PlaylistTracksState {
    object Empty : PlaylistTracksState
    data class Content(val tracks: List<Track>) : PlaylistTracksState
}