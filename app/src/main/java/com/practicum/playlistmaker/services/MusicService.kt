package com.practicum.playlistmaker.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.di.TRACK_TIME_CLIENT
import com.practicum.playlistmaker.domain.api.player.AudioPlayerEventListener
import com.practicum.playlistmaker.domain.api.player.AudioPlayerInteractor
import com.practicum.playlistmaker.ui.audioplayer.view_model.PlayerState
import com.practicum.playlistmaker.ui.models.TrackParcelable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat

internal class MusicService() : Service(),
    AudioPlayerEventListener {
    private val audioPlayerInteractor: AudioPlayerInteractor by inject()
    private val dateFormatter: SimpleDateFormat by inject(TRACK_TIME_CLIENT)
    private var track: TrackParcelable? = null
    private val binder by lazy { MusicServiceBinder() }
    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Default)
    val playerState = _playerState.asStateFlow()
    private var foregroundDisplaying = false

    override fun onBind(intent: Intent?): IBinder {
        init(intent)
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        audioPlayerInteractor.setStateListener(null)
        audioPlayerInteractor.onRelease()
        super.onDestroy()
    }

    override fun providePlayerState(): StateFlow<PlayerState> = playerState

    private fun init(intent: Intent?) {
        track = intent?.getParcelableExtra(ARG_TRACK, TrackParcelable::class.java)
        audioPlayerInteractor.setStateListener(this)
        track?.previewUrl?.let {
            audioPlayerInteractor.preparePlayer(it)
        }
        _playerState.value = PlayerState.TimeProgress(dateFormatter.format(INITIAL_PROGRESS))
    }

    override fun startForeground() {
        if (!foregroundDisplaying) {
            ServiceCompat.startForeground(
                this,
                SERVICE_NOTIFICATION_ID,
                createNotification(),
                getForegroundServiceTypeConstant()
            )
            foregroundDisplaying = true
        }
    }

    override fun stopForeground() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        foregroundDisplaying = false
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        channel.description = CHANNEL_DESCRIPTION

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentText("${track?.artistName ?: ""} — ${track?.trackName ?: ""}")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun getForegroundServiceTypeConstant(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }
    }

    override fun onPlayerPrepared() {
        _playerState.value = PlayerState.Prepared
    }

    override fun onPlayerCompletion() {
        _playerState.value = PlayerState.Complete
        stopForeground()
    }

    override fun onPlayerChangePosition(position: Int) {
        _playerState.value = PlayerState.TimeProgress(dateFormatter.format(position))
    }

    override fun playbackControl() {
        audioPlayerInteractor.playbackControl()
    }

    inner class MusicServiceBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "music_service_channel"
        private const val SERVICE_NOTIFICATION_ID = 1
        const val ARG_TRACK = "TRACK"
        private const val INITIAL_PROGRESS = 0
        private const val CHANNEL_NAME = "Music Service"
        private const val CHANNEL_DESCRIPTION = "Music playing service"
        private const val NOTIFICATION_TITLE = "PlaylistMaker"
    }
}