package com.practicum.playlistmaker.domain.api.player

interface AudioPlayerRepository {
    fun preparePlayer(dataSource: String?)
    fun pausePlayer()
    fun playbackControl()
    fun onRelease()
    fun setStateListener(listener: AudioPlayerEventListener?)
}