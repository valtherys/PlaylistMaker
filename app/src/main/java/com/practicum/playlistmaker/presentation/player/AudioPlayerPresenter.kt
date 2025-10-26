package com.practicum.playlistmaker.presentation.player

import com.practicum.playlistmaker.domain.api.player.AudioPlayerEventListener
import com.practicum.playlistmaker.domain.api.player.AudioPlayerInteractor
import com.practicum.playlistmaker.presentation.ui.audioplayer.AudioPlayerView


class AudioPlayerPresenter(private val interactor: AudioPlayerInteractor): AudioPlayerEventListener {
    private var view: AudioPlayerView? = null

    fun attach(view: AudioPlayerView) {
        this.view = view
        interactor.setStateListener(this)
    }

    fun detach() {
        this.view = null
        interactor.setStateListener(null)
    }

    fun onPlayerPrepare(url: String?){
        interactor.preparePlayer(url)
    }

    fun onPlayerPlaybackControl(){
        interactor.playbackControl()
    }

    fun onPause(){
        interactor.pausePlayer()
    }

    fun onReleaseResources(){
        interactor.removeCallbacks()
        interactor.release()
    }

    override fun onPlayerPrepared() {
        view?.onPlayerPrepared()
    }

    override fun onPlayerStart() {
        view?.onPlayerStart()
    }

    override fun onPlayerPause() {
        view?.onPlayerPause()
    }

    override fun onPlayerCompletion() {
        view?.onPlayerCompletion()
    }

    override fun onPlayerChangePosition(position: Int) {
        view?.onPlayerChangePosition(position)
    }
}