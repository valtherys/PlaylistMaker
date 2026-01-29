package com.practicum.playlistmaker.domain.impl.db

import com.practicum.playlistmaker.domain.api.db.FavoritesInteractor
import com.practicum.playlistmaker.domain.api.db.FavoritesRepository
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.models.TrackParcelable
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(private val repository: FavoritesRepository) : FavoritesInteractor {
    override suspend fun addTrackToFavorites(track: TrackParcelable) {
        repository.addTrackToFavorites(track)
    }

    override suspend fun deleteTrackFromFavorites(track: TrackParcelable) {
        repository.deleteTrackFromFavorites(track)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return repository.getFavoriteTracks()
    }

    override fun checkTrackIsFavorite(trackId: String): Flow<String?> {
        return repository.checkTrackIsFavorite(trackId)
    }


}