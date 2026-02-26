package com.practicum.playlistmaker.domain.api.db

import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.models.TrackParcelable
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun addPlaylistToDb(playlist: Playlist): Boolean
    suspend fun addTrackInPlaylist(track: TrackParcelable, playlist: Playlist): Boolean
    fun getPlaylistsFromDb(): Flow<List<Playlist>?>
    suspend fun deletePlaylists()
    fun getPlaylist(id: Int): Flow<Playlist>
    fun getPlaylistTracks(playlistId: List<String>): Flow<List<Track>>
    suspend fun deleteTrackFromPlaylist(track: Track, playlist: Playlist): Boolean
    suspend fun deletePlaylist(playlistId: Int): Boolean
}