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

    override fun getPlaylistTracks(ids: List<String>): Flow<List<Track>> {
        val tracksEntity = playlistTrackDao.getPlaylistTracks(ids).distinctUntilChanged()
        val tracks =
            tracksEntity.map { tracks -> tracks.map { track -> playlistTrackDbMapper.map(track) } }
        return tracks
    }

    suspend fun deleteTrack(track: Track): Boolean {
        val trackEntity = playlistTrackDbMapper.map(track)
        val res = playlistTrackDao.deleteTrack(trackEntity)
        return res > ROWS_UNUPDATED
    }

    override suspend fun deleteTrackFromPlaylist(track: Track, playlist: Playlist): Boolean {
        val updatedPlaylist = playlist.copy(
            trackIds = playlist.trackIds?.filter { it != track.trackId },
            tracksAmount = (playlist.trackIds?.size ?: 0) - 1
        )
        val playlistEntity = playlistDbMapper.map(updatedPlaylist)
        val playlistsWithTrack = playlistDao.countPlaylistsContainingTrack(track.trackId)
        if (playlistsWithTrack == ONE_PLAYLIST_WITH_TRACK) {
            deleteTrack(track)
        }
        val res = playlistDao.updatePlaylist(playlistEntity)
        return res > ROWS_UNUPDATED
    }

    override suspend fun deletePlaylist(id: Int): Boolean {
        val playlist = playlistDao.getPlaylist(id).first()
        val trackIds = playlist.trackIds ?: listOf()
        val playlistEntity = playlistDbMapper.map(playlist)

        for (trackId in trackIds) {
            val playlistsWithTrack = playlistDao.countPlaylistsContainingTrack(trackId)
            if (playlistsWithTrack == ONE_PLAYLIST_WITH_TRACK) {
                playlistTrackDao.deleteTrackById(trackId)
            }
        }
        val res = playlistDao.deletePlaylist(playlistEntity)
        return res > ROWS_UNUPDATED
    }

    companion object {
        private const val ROWS_UNUPDATED = 0
        private const val ONE_PLAYLIST_WITH_TRACK = 1
        private const val INSERT_CONFLICT = -1f
    }
}