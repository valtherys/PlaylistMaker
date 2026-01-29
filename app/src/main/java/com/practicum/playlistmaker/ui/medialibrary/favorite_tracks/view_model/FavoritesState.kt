package com.practicum.playlistmaker.ui.medialibrary.favorite_tracks.view_model

import com.practicum.playlistmaker.domain.models.Track

sealed interface FavoritesState {
    data class Content(val favoriteTracks: List<Track>): FavoritesState
    data class Empty(val message: String): FavoritesState
}