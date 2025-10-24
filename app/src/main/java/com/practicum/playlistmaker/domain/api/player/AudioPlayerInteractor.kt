package com.practicum.playlistmaker.domain.api.player


interface AudioPlayerInteractor {
    fun preparePlayer(dataSource: String?)
    fun pausePlayer()
    fun playbackControl()
    fun release()
    fun setStateListener(listener: AudioPlayerEventListener?)
    fun removeCallbacks()
}