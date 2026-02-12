package com.practicum.playlistmaker.data.db

import com.practicum.playlistmaker.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.data.mappers.PlaylistDbMapper
import com.practicum.playlistmaker.domain.api.db.PlaylistsRepository
import com.practicum.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistDbMapper: PlaylistDbMapper
) : PlaylistsRepository {
    override suspend fun addPlaylist(playlist: Playlist): Long {
        val playlistEntity = playlistDbMapper.map(playlist)
        return playlistDao.addPlaylist(playlistEntity)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbMapper.map(playlist)
        playlistDao.updatePlaylist(playlistEntity)
    }

    override fun getPlaylists(): Flow<List<Playlist>?> =
        playlistDao.getPlaylists()
            .map { playlists -> playlists?.map { playlist -> playlistDbMapper.map(playlist) } }

    override suspend fun deleteTable() {
        playlistDao.deleteAll()
    }
}