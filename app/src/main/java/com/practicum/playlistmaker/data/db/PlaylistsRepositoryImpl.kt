package com.practicum.playlistmaker.data.db

import com.practicum.playlistmaker.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.data.db.dao.PlaylistTrackDao
import com.practicum.playlistmaker.data.mappers.PlaylistDbMapper
import com.practicum.playlistmaker.data.mappers.PlaylistTrackDbMapper
import com.practicum.playlistmaker.domain.api.db.PlaylistsRepository
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.models.TrackParcelable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
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
        playlistDao.getPlaylists().distinctUntilChanged()
            .map { playlists -> playlists?.map { playlist -> playlistDbMapper.map(playlist) } }

    override suspend fun deleteTable() {
        playlistDao.deleteAll()
    }

    override fun getPlaylist(id: Int): Flow<Playlist> {
        return playlistDao.getPlaylist(id).distinctUntilChanged()
    }

    override suspend fun getPlaylistTracks(playlistId: Int): Flow<List<Track>> {
        val playlistEntity = playlistDao.getPlaylist(playlistId).first()
        val tracksEntity = playlistTrackDao.getPlaylistTracks(playlistEntity.trackIds ?: listOf())
            .distinctUntilChanged()
        val tracks =
            tracksEntity.map { tracks -> tracks.map { track -> playlistTrackDbMapper.map(track) } }
        return tracks
    }

    override suspend fun deleteTrackFromPlaylist(trackId: String, playlistId: Int): Boolean {
        val playlist = playlistDao.getPlaylist(playlistId).first()
        val updatedPlaylist = playlist.copy(
            trackIds = playlist.trackIds?.filter { it != trackId },
            tracksAmount = (playlist.trackIds?.size ?: 0) - 1
        )
        val updatedPlaylistEntity = playlistDbMapper.map(updatedPlaylist)

        deleteUniqueTracks(trackId)
        val res = playlistDao.updatePlaylist(updatedPlaylistEntity)
        return res > ROWS_UNUPDATED
    }

    override suspend fun deletePlaylist(id: Int): Boolean {
        val playlist = playlistDao.getPlaylist(id).first()
        val trackIds = playlist.trackIds ?: listOf()

        for (trackId in trackIds) {
            deleteUniqueTracks(trackId)
        }
        val res = playlistDao.deletePlaylistById(playlist.playlistId!!)
        return res > ROWS_UNUPDATED
    }

    private suspend fun deleteUniqueTracks(trackId: String) {
        val playlistsWithTrack = playlistDao.countPlaylistsContainingTrack(trackId)
        if (playlistsWithTrack == ONE_PLAYLIST_WITH_TRACK) {
            playlistTrackDao.deleteTrackById(trackId)
        }
    }

    companion object {
        private const val ROWS_UNUPDATED = 0
        private const val ONE_PLAYLIST_WITH_TRACK = 1
        private const val INSERT_CONFLICT = -1f
    }
}