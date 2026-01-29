package com.practicum.playlistmaker.ui.medialibrary.favorite_tracks.view_model

import androidx.annotation.StringRes
import com.practicum.playlistmaker.domain.models.Track

sealed interface FavoritesState {
    data class Content(val favoriteTracks: List<Track>): FavoritesState
    data class Empty(@StringRes val messageResId: Int): FavoritesState
}