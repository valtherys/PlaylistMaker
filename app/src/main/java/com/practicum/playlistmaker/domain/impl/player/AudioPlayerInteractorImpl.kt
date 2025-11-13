package com.practicum.playlistmaker.domain.impl.player

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

    override fun onRelease() {
        repository.onRelease()
    }

    override fun setStateListener(listener: AudioPlayerEventListener?) {
        repository.setStateListener(listener)
    }
}