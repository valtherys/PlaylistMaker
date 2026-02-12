package com.practicum.playlistmaker.domain.impl.db

import com.practicum.playlistmaker.domain.api.db.PlaylistsInteractor
import com.practicum.playlistmaker.domain.api.db.PlaylistsRepository
import com.practicum.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(private val repository: PlaylistsRepository) : PlaylistsInteractor {
    override suspend fun addPlaylistToDb(playlist: Playlist): Long {
        return repository.addPlaylist(playlist)
    }

    override suspend fun updatePlaylistInDb(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override fun getPlaylistsFromDb(): Flow<List<Playlist>?> = repository.getPlaylists()
    override suspend fun deletePlaylists() {
        repository.deleteTable()
    }
}