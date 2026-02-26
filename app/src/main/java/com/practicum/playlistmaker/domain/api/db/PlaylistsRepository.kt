package com.practicum.playlistmaker.domain.api.db

import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.models.TrackParcelable
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    suspend fun addPlaylist(playlist: Playlist): Boolean
    suspend fun addTrackInPlaylist(track: TrackParcelable, playlist: Playlist): Boolean
    fun getPlaylists(): Flow<List<Playlist>?>
    suspend fun deleteTable()
    fun getPlaylist(id: Int): Flow<Playlist>
    fun getPlaylistTracks(ids: List<String>): Flow<List<Track>>
    suspend fun deleteTrackFromPlaylist(track: Track, playlist: Playlist): Boolean
    suspend fun deletePlaylist(id: Int): Boolean
}