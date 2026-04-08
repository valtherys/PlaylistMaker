package com.practicum.playlistmaker.domain.api.player

import com.practicum.playlistmaker.ui.audioplayer.view_model.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerEventListener {
    fun providePlayerState(): StateFlow<PlayerState>
    fun playbackControl()
    fun onPlayerPrepared()
    fun onPlayerCompletion()
    fun onPlayerChangePosition(position: Int)
    fun startForeground()
    fun stopForeground()
}