package com.practicum.playlistmaker.ui.audioplayer.view_model

import com.practicum.playlistmaker.domain.models.Playlist

sealed interface PlaylistsState {
    data class Playlists(val playlists: List<Playlist>) : PlaylistsState
    object Empty : PlaylistsState
}