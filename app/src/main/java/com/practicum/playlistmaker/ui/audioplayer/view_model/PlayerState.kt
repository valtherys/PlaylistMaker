package com.practicum.playlistmaker.ui.audioplayer.view_model

import androidx.annotation.StringRes
import com.practicum.playlistmaker.domain.models.Playlist

sealed interface PlayerState {
    object Default : PlayerState
    object Prepared : PlayerState
    object Playing : PlayerState
    object Paused : PlayerState
    object Complete : PlayerState
    data class TimeProgress(val progress: String) : PlayerState
    data class Favorite(val isFavorite: Boolean = false) : PlayerState
}
