package com.practicum.playlistmaker.domain.api.db

import com.practicum.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun addPlaylistToDb(playlist: Playlist): Long
    suspend fun updatePlaylistInDb(playlist: Playlist)
    fun getPlaylistsFromDb(): Flow<List<Playlist>?>
    suspend fun deletePlaylists()
}