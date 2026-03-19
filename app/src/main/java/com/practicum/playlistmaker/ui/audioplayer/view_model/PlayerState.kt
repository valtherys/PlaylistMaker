package com.practicum.playlistmaker.ui.audioplayer.view_model

sealed interface PlayerState {
    object Default : PlayerState
    object Prepared : PlayerState
    object Complete : PlayerState
    data class TimeProgress(val progress: String) : PlayerState
    data class Favorite(val isFavorite: Boolean = false) : PlayerState
}
