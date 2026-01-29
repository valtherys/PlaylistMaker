package com.practicum.playlistmaker.domain.api.db

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.models.TrackParcelable
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    suspend fun addTrackToFavorites(track: TrackParcelable)
    suspend fun deleteTrackFromFavorites(track: TrackParcelable)
    fun getFavoriteTracks(): Flow<List<Track>>
    fun checkTrackIsFavorite(trackId: String): Flow<String?>
}