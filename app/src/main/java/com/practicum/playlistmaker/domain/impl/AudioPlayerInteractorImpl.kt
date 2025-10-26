package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.player.AudioPlayerEventListener
import com.practicum.playlistmaker.domain.api.player.AudioPlayerInteractor
import com.practicum.playlistmaker.domain.api.player.AudioPlayerRepository

class AudioPlayerInteractorImpl(private val repository: AudioPlayerRepository) :
    AudioPlayerInteractor {

    override fun preparePlayer(dataSource: String?) {
        repository.preparePlayer(dataSource)
    }

    override fun pausePlayer() {
        repository.pausePlayer()
    }

    override fun playbackControl() {
        repository.playbackControl()
    }

    override fun release() {
        repository.release()
    }

    override fun setStateListener(listener: AudioPlayerEventListener?) {
        repository.setStateListener(listener)
    }

    override fun removeCallbacks() {
        repository.removeCallbacks()
    }
}