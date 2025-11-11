package com.practicum.playlistmaker.ui.audioplayer.view_model

sealed interface PlayerState {
    object Default : PlayerState
    object Prepared : PlayerState
    object Playing : PlayerState
    object Paused : PlayerState
    object Complete: PlayerState
}