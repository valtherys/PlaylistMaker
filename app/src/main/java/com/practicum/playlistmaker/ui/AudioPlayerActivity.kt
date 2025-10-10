package com.practicum.playlistmaker.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.model.Track
import com.practicum.playlistmaker.utils.applySystemBarsPadding
import com.practicum.playlistmaker.utils.dpToPx
import androidx.constraintlayout.widget.Group
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioPlayerBinding
    private var track: Track? = null
    private val albumCornerRadiusDp: Float = ALBUM_CORNER_RADIUS_DP
    private val mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private var mainThreadHandler = Handler(Looper.getMainLooper())
    private val dateFormat by lazy {
        SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        )
    }
    private var lastCurrentPosition = LAST_CURRENT_POSITION_DEFAULT

    private val updateTimerTask = object : Runnable {
        override fun run() {
            val currentPosition = mediaPlayer.currentPosition
            if (currentPosition != lastCurrentPosition) {
                binding.tvTimer.text = dateFormat.format(currentPosition)
                lastCurrentPosition = currentPosition
            }
            if (playerState == STATE_PLAYING && mediaPlayer.isPlaying) {
                mainThreadHandler.postDelayed(this, TIMER_UPDATE_DELAY)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applySystemBarsPadding()

        track = intent.getParcelableExtra(
            INTENT_EXTRA_KEY
        )
        if (track == null) {
            Log.e(ACTIVITY_TAG, "No track passed to activity")
            finish()
            return
        }

        val albumCornerRadiusPx = dpToPx(albumCornerRadiusDp)

        binding.btnPlay.isEnabled = false
        preparePlayer()
        bindData(track!!, albumCornerRadiusPx)
        binding.btnPlay.setOnClickListener {
            playbackControl()
        }
        binding.btnBack.setOnClickListener { finish() }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler.removeCallbacksAndMessages(null)
        mediaPlayer.release()
    }

    private fun bindData(track: Track, cornerRadiusPx: Int) {
        val albumCoverHighResolution = track.getArtworkUrlHighResolution()
        Glide.with(this)
            .load(albumCoverHighResolution)
            .placeholder(R.drawable.ic_placeholder_45)
            .transform(RoundedCorners(cornerRadiusPx)).into(binding.ivAlbumCover)

        binding.tvTrackName.text = track.trackName
        binding.tvTrackArtist.text = track.artistName
        binding.tvDurationData.text = track.trackTime
        binding.tvGenreData.text = track.primaryGenreName
        binding.tvCountryData.text = track.country

        hideViewGroupIfEmpty(track.collectionName, binding.groupAlbum, binding.tvAlbumData)
        hideViewGroupIfEmpty(track.getReleaseYear(), binding.groupYear, binding.tvYearData)
    }

    private fun hideViewGroupIfEmpty(
        field: String?,
        viewGroup: Group,
        dataField: TextView
    ) {
        if (field.isNullOrEmpty()) {
            viewGroup.visibility = View.GONE
        } else {
            viewGroup.visibility = View.VISIBLE
            dataField.text = field
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setOnPreparedListener {
            binding.btnPlay.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            mainThreadHandler.removeCallbacks(updateTimerTask)
            binding.btnPlay.setImageResource(R.drawable.ic_play_100)
            binding.tvTimer.text = getString(R.string.count_start)
            playerState = STATE_PREPARED
            lastCurrentPosition = LAST_CURRENT_POSITION_DEFAULT
        }

        mediaPlayer.setDataSource(track?.previewUrl)
        mediaPlayer.prepareAsync()
    }

    private fun startPlayer() {
        mediaPlayer.start()
        lastCurrentPosition = LAST_CURRENT_POSITION_DEFAULT
        mainThreadHandler.post(updateTimerTask)
        binding.btnPlay.setImageResource(R.drawable.ic_pause_100)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            mainThreadHandler.removeCallbacks(updateTimerTask)
            binding.btnPlay.setImageResource(R.drawable.ic_play_100)
            playerState = STATE_PAUSED
        }
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }


    companion object {
        const val INTENT_EXTRA_KEY = "TRACK"
        private const val ACTIVITY_TAG = "AudioPlayerActivity"
        private const val TIMER_UPDATE_DELAY = 300L
        private const val LAST_CURRENT_POSITION_DEFAULT = -1
        private const val ALBUM_CORNER_RADIUS_DP = 8f
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}