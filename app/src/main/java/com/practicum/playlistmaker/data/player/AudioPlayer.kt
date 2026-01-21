package com.practicum.playlistmaker.data.player

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.api.player.AudioPlayerEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AudioPlayer(var mediaPlayer: MediaPlayer) {
    private var playerState = PlayerState.STATE_DEFAULT

    private var listener: AudioPlayerEventListener? = null

    private val scope = CoroutineScope(Dispatchers.Default)
    private var updateTimerJob: Job? = null
    private val positionFlow = flow {
        while (mediaPlayer.isPlaying && playerState == PlayerState.STATE_PLAYING) {
            val currentPosition = mediaPlayer.currentPosition
            emit(currentPosition)
            delay(TIMER_UPDATE_DELAY)
        }
    }

    fun startTimer() {
        updateTimerJob?.cancel()
        updateTimerJob = scope.launch {
            withContext(Dispatchers.Main) {
                positionFlow.distinctUntilChanged()
                    .collect { positionFlow -> listener?.onPlayerChangePosition(positionFlow) }
            }
        }
    }

    fun setStateListener(listener: AudioPlayerEventListener?) {
        this.listener = listener
    }

    fun preparePlayer(dataSource: String?) {
        mediaPlayer.setOnPreparedListener {
            listener?.onPlayerPrepared()
            playerState = PlayerState.STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            updateTimerJob?.cancel()
            listener?.onPlayerCompletion()
            playerState = PlayerState.STATE_PREPARED
        }

        mediaPlayer.setDataSource(dataSource)
        mediaPlayer.prepareAsync()
    }

    fun startPlayer() {
        mediaPlayer.start()
        startTimer()
        playerState = PlayerState.STATE_PLAYING
        listener?.onPlayerStart()
    }

    fun pausePlayer() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            updateTimerJob?.cancel()
            playerState = PlayerState.STATE_PAUSED
            listener?.onPlayerPause()
        }
    }

    fun playbackControl() {
        when (playerState) {
            PlayerState.STATE_PLAYING -> {
                pausePlayer()
            }

            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                startPlayer()
            }

            PlayerState.STATE_DEFAULT -> Unit
        }
    }

    fun onRelease() {
        updateTimerJob?.cancel()
        scope.cancel()
        mediaPlayer.release()
    }

    companion object {
        private const val TIMER_UPDATE_DELAY = 300L
    }
}