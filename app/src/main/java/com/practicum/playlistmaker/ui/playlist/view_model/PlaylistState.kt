package com.practicum.playlistmaker.ui.playlist.view_model

import com.practicum.playlistmaker.domain.models.Playlist

data class PlaylistState(
    val playlist: Playlist? = null,
    val tracksState: PlaylistTracksState = PlaylistTracksState.Empty,
    val duration: Int = 0
)