package com.practicum.playlistmaker.ui.medialibrary.playlists.view_model

import com.practicum.playlistmaker.domain.models.Playlist

sealed interface PlaylistsState {
    object Empty : PlaylistsState
    data class Content(val playlists: List<Playlist>) : PlaylistsState
}