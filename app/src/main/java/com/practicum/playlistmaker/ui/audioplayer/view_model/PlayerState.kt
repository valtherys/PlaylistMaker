package com.practicum.playlistmaker.ui.audioplayer.view_model

sealed interface PlayerState {
    object Default : PlayerState
    object Prepared : PlayerState
    object Playing : PlayerState
    object Paused : PlayerState
    object Complete : PlayerState
    data class TimeProgress(val progress: String = "00:00") : PlayerState
}