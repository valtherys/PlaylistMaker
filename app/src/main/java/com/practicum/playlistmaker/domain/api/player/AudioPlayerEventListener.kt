package com.practicum.playlistmaker.domain.api.player

interface AudioPlayerEventListener {
    fun onPlayerPrepared()
    fun onPlayerStart()
    fun onPlayerPause()
    fun onPlayerCompletion()
    fun onPlayerChangePosition(position: Int)
}