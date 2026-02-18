package com.practicum.playlistmaker.data.db

import com.practicum.playlistmaker.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.data.db.dao.PlaylistTrackDao
import com.practicum.playlistmaker.data.mappers.PlaylistDbMapper
import com.practicum.playlistmaker.data.mappers.PlaylistTrackDbMapper
import com.practicum.playlistmaker.domain.api.db.PlaylistsRepository
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.ui.models.TrackParcelable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val playlistDbMapper: PlaylistDbMapper,
    private val playlistTrackDbMapper: PlaylistTrackDbMapper,
) : PlaylistsRepository {
    override suspend fun addPlaylist(playlist: Playlist): Boolean {
        val playlistEntity = playlistDbMapper.map(playlist)
        return playlistDao.addPlaylist(playlistEntity) > ROWS_UNUPDATED
    }

    override suspend fun addTrackInPlaylist(track: TrackParcelable, playlist: Playlist): Boolean {
        val playlistTracks = (playlist.trackIds ?: listOf()) + track.trackId
        val updatedPlaylist = playlist.copy(
            trackIds = playlistTracks,
            tracksAmount = (playlist.trackIds?.size ?: 0) + 1
        )

        val playlistEntity = playlistDbMapper.map(updatedPlaylist)
        val trackPlaylistEntity = playlistTrackDbMapper.map(track)
        val resPlaylist = playlistDao.updatePlaylist(playlistEntity)
        val resTrack = playlistTrackDao.addTrack(trackPlaylistEntity)

        return (resPlaylist > ROWS_UNUPDATED && (resTrack >= INSERT_CONFLICT))
    }

    override fun getPlaylists(): Flow<List<Playlist>?> =
        playlistDao.getPlaylists()
            .map { playlists -> playlists?.map { playlist -> playlistDbMapper.map(playlist) } }

    override suspend fun deleteTable() {
        playlistDao.deleteAll()
    }

    companion object {
        private const val ROWS_UNUPDATED = 0
        private const val INSERT_CONFLICT = -1f
    }
}