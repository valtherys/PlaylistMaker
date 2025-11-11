package com.practicum.playlistmaker.data.player

import com.practicum.playlistmaker.domain.api.player.AudioPlayerEventListener
import com.practicum.playlistmaker.domain.api.player.AudioPlayerRepository


class AudioPlayerRepositoryImpl(private val audioPlayer: AudioPlayer) : AudioPlayerRepository {
    override fun preparePlayer(dataSource: String?) {
        audioPlayer.preparePlayer(dataSource)
    }

    override fun setStateListener(listener: AudioPlayerEventListener?) {
        audioPlayer.setStateListener(listener)
    }

    override fun pausePlayer() {
        audioPlayer.pausePlayer()
    }

    override fun playbackControl() {
        audioPlayer.playbackControl()
    }

    override fun onRelease() {
        audioPlayer.onRelease()
    }
}