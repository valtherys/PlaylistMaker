package com.practicum.playlistmaker.ui.audioplayer.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.api.player.AudioPlayerEventListener
import com.practicum.playlistmaker.domain.api.player.AudioPlayerInteractor
import java.text.SimpleDateFormat

class AudioPlayerViewModel(
    private val interactor: AudioPlayerInteractor,
    private val dateFormatter: SimpleDateFormat,
    private val previewUrl: String
) : ViewModel(), AudioPlayerEventListener {
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default)
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    init {
        interactor.setStateListener(this)
        interactor.preparePlayer(previewUrl)
        playerStateLiveData.value = PlayerState.TimeProgress(dateFormatter.format(0))
    }

    override fun onPlayerPrepared() {
        playerStateLiveData.postValue(PlayerState.Prepared)
    }

    override fun onPlayerStart() {
        playerStateLiveData.postValue(PlayerState.Playing)
    }

    override fun onPlayerCompletion() {
        playerStateLiveData.postValue(PlayerState.Complete)
    }

    override fun onPlayerChangePosition(position: Int) {
        playerStateLiveData.value = PlayerState.TimeProgress(dateFormatter.format(position))
    }

    override fun onPlayerPause() {
        playerStateLiveData.postValue(PlayerState.Paused)
    }

    fun onPlayClicked() {
        interactor.playbackControl()
    }

    fun onPause() {
        interactor.pausePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        interactor.setStateListener(null)
        interactor.onRelease()
    }
}