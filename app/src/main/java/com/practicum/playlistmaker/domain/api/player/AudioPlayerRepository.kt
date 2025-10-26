package com.practicum.playlistmaker.domain.api.player

interface AudioPlayerRepository {
    fun preparePlayer(dataSource: String?)
    fun pausePlayer()
    fun playbackControl()
    fun release()
    fun setStateListener(listener: AudioPlayerEventListener?)
    fun removeCallbacks()
}