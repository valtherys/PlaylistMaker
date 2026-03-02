package com.practicum.playlistmaker.domain.impl.db

import com.practicum.playlistmaker.domain.api.db.PlaylistsInteractor
import com.practicum.playlistmaker.domain.api.db.PlaylistsRepository
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.models.TrackParcelable
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(private val repository: PlaylistsRepository) : PlaylistsInteractor {
    override suspend fun addPlaylistToDb(playlist: Playlist): Boolean {
        return repository.addPlaylist(playlist)
    }

    override suspend fun addTrackInPlaylist(track: TrackParcelable, playlist: Playlist): Boolean {
        return repository.addTrackInPlaylist(track, playlist)
    }

    override fun getPlaylistsFromDb(): Flow<List<Playlist>?> = repository.getPlaylists()

    override suspend fun deletePlaylists() {
        repository.deleteTable()
    }

    override fun getPlaylist(id: Int): Flow<Playlist> {
        return repository.getPlaylist(id)
    }

    override suspend fun getPlaylistTracks(playlistId: Int): Flow<List<Track>> {
        return repository.getPlaylistTracks(playlistId)
    }

    override suspend fun deleteTrackFromPlaylist(
        trackId: String, playlistId: Int
    ): Boolean {
        return repository.deleteTrackFromPlaylist(trackId, playlistId)
    }

    override suspend fun deletePlaylist(playlistId: Int): Boolean {
        return repository.deletePlaylist(playlistId)
    }
}