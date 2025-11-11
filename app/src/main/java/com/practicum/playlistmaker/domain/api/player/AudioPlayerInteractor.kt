package com.practicum.playlistmaker.domain.api.player


interface AudioPlayerInteractor {
    fun preparePlayer(dataSource: String?)
    fun pausePlayer()
    fun playbackControl()
    fun onRelease()
    fun setStateListener(listener: AudioPlayerEventListener?)
}