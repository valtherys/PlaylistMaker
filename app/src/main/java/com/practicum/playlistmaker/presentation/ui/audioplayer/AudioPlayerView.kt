package com.practicum.playlistmaker.presentation.ui.audioplayer

interface AudioPlayerView {
    fun onPlayerPrepared()
    fun onPlayerStart()
    fun onPlayerPause()
    fun onPlayerCompletion()
    fun onPlayerChangePosition(position: Int)
}