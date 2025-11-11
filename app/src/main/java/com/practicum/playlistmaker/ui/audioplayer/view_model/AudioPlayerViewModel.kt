package com.practicum.playlistmaker.ui.audioplayer.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.api.player.AudioPlayerEventListener
import com.practicum.playlistmaker.domain.api.player.AudioPlayerInteractor
import java.text.SimpleDateFormat
import java.util.Locale


class AudioPlayerViewModel(
    private val interactor: AudioPlayerInteractor,
    private val previewUrl: String
) : ViewModel(), AudioPlayerEventListener {
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default)
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private val progressTimeLiveData = MutableLiveData("00:00")
    fun observeProgressTime(): LiveData<String> = progressTimeLiveData

    private val dateFormat by lazy {
        SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        )
    }

    init {
        interactor.setStateListener(this)
        interactor.preparePlayer(previewUrl)
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
        progressTimeLiveData.value = dateFormat.format(position)
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

    companion object {
        fun getFactory(previewUrl: String): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    val interactor = Creator.provideAudioPlayerInteractor()
                    AudioPlayerViewModel(interactor, previewUrl)
                }
            }
        }
    }
}