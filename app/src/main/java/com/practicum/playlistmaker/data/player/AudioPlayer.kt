package com.practicum.playlistmaker.data.player

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.practicum.playlistmaker.domain.api.player.AudioPlayerEventListener

class AudioPlayer(var mediaPlayer: MediaPlayer) {
    private var playerState = PlayerState.STATE_DEFAULT

    private var listener: AudioPlayerEventListener? = null
    private var mainThreadHandler = Handler(Looper.getMainLooper())

    private var lastCurrentPosition = LAST_CURRENT_POSITION_DEFAULT

    private val updateTimerTask = object : Runnable {
        override fun run() {
            val currentPosition = mediaPlayer.currentPosition
            if (currentPosition != lastCurrentPosition) {
                listener?.onPlayerChangePosition(currentPosition)
                lastCurrentPosition = currentPosition
            }
            if (playerState == PlayerState.STATE_PLAYING && mediaPlayer.isPlaying) {
                mainThreadHandler.postDelayed(this, TIMER_UPDATE_DELAY)
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
            mainThreadHandler.removeCallbacks(updateTimerTask)
            listener?.onPlayerCompletion()
            playerState = PlayerState.STATE_PREPARED
            lastCurrentPosition = LAST_CURRENT_POSITION_DEFAULT
        }

        mediaPlayer.setDataSource(dataSource)
        mediaPlayer.prepareAsync()
    }

    fun startPlayer() {
        mediaPlayer.start()
        lastCurrentPosition = LAST_CURRENT_POSITION_DEFAULT
        mainThreadHandler.post(updateTimerTask)
        playerState = PlayerState.STATE_PLAYING
        listener?.onPlayerStart()
    }

    fun pausePlayer() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            mainThreadHandler.removeCallbacks(updateTimerTask)
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

    fun onRelease(){
        mainThreadHandler.removeCallbacksAndMessages(null)
        mediaPlayer.release()
    }

    companion object {
        private const val TIMER_UPDATE_DELAY = 300L
        private const val LAST_CURRENT_POSITION_DEFAULT = -1
    }
}