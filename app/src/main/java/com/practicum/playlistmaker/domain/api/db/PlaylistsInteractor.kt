package com.practicum.playlistmaker.domain.api.db

import com.practicum.playlistmaker.domain.models.Playlist

interface PlaylistsInteractor {
    suspend fun addPlaylistToDb(playlist: Playlist): Long
    suspend fun updatePlaylistInDb(playlist: Playlist)
}