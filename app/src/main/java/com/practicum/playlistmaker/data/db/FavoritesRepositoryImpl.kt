package com.practicum.playlistmaker.data.db

import com.practicum.playlistmaker.data.db.dao.TrackDao
import com.practicum.playlistmaker.data.mappers.TrackDbMapper
import com.practicum.playlistmaker.domain.api.db.FavoritesRepository
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.models.TrackParcelable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val trackDao: TrackDao,
    private val trackDbMapper: TrackDbMapper
) : FavoritesRepository {
    override suspend fun addTrackToFavorites(track: TrackParcelable) {
        val trackEntity = trackDbMapper.map(track)
        trackDao.insertTrack(trackEntity)
    }

    override suspend fun deleteTrackFromFavorites(track: TrackParcelable) {
        val trackEntity = trackDbMapper.map(track)
        trackDao.deleteTrack(trackEntity)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> = trackDao
        .getTracks()
        .map { list -> list.map { trackDbMapper.map(it) } }

    override fun checkTrackIsFavorite(trackId: String): Flow<String?> {
        return trackDao.findTrackInDb(trackId)
    }

}
