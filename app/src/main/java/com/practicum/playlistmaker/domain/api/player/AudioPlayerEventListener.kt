package com.practicum.playlistmaker.domain.api.player

interface AudioPlayerEventListener {
    fun onPlayerPrepared()
    fun onPlayerCompletion()
    fun onPlayerChangePosition(position: Int)
}