package com.practicum.playlistmaker.ui.audioplayer.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.practicum.playlistmaker.ui.audioplayer.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.ui.audioplayer.view_model.PlayerState
import com.practicum.playlistmaker.ui.models.TrackParcelable
import com.practicum.playlistmaker.utils.applySystemBarsPadding
import com.practicum.playlistmaker.utils.dpToPx

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioPlayerBinding
    private var track: TrackParcelable? = null
    private val albumCornerRadiusDp: Float = ALBUM_CORNER_RADIUS_DP

    private val viewModel: AudioPlayerViewModel by viewModels {
        val previewUrl = track?.previewUrl
        AudioPlayerViewModel.Companion.getFactory(previewUrl ?: "")
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

        bindData(track!!, albumCornerRadiusPx)

        binding.btnPlay.setOnClickListener {
            viewModel.onPlayClicked()
        }

        viewModel.observePlayerState().observe(this) {
            render(it)
        }

        binding.btnBack.setOnClickListener { finish() }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun bindData(track: TrackParcelable, cornerRadiusPx: Int) {
        val albumCoverHighResolution = track.getArtworkUrlHighResolution()
        Glide.with(this)
            .load(albumCoverHighResolution)
            .placeholder(R.drawable.ic_placeholder_45)
            .transform(RoundedCorners(cornerRadiusPx)).into(binding.ivAlbumCover)

        binding.apply {
            tvTrackName.text = track.trackName
            tvTrackArtist.text = track.artistName
            tvDurationData.text = track.trackTime
            tvGenreData.text = track.primaryGenreName
            tvCountryData.text = track.country
        }

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

    fun onPlayerStart() {
        binding.btnPlay.setImageResource(R.drawable.ic_pause_100)
    }

    fun onPlayerChangePosition(position: String) {
        binding.tvTimer.text = position
    }

    fun onPlayerPause() {
        binding.btnPlay.setImageResource(R.drawable.ic_play_100)
    }

    fun onPlayerPrepared() {
        binding.btnPlay.isEnabled = true
    }

    fun onPlayerCompletion() {
        binding.apply {
            btnPlay.setImageResource(R.drawable.ic_play_100)
            tvTimer.text = getString(R.string.count_start)
        }
    }

    fun render(state: PlayerState) {
        when (state) {
            PlayerState.Default -> binding.btnPlay.isEnabled = false
            PlayerState.Paused -> onPlayerPause()
            PlayerState.Playing -> onPlayerStart()
            PlayerState.Prepared -> onPlayerPrepared()
            PlayerState.Complete -> onPlayerCompletion()
            is PlayerState.TimeProgress -> onPlayerChangePosition(state.progress)
        }
    }

    companion object {
        const val INTENT_EXTRA_KEY = "TRACK"
        private const val ACTIVITY_TAG = "AudioPlayerActivity"
        private const val ALBUM_CORNER_RADIUS_DP = 8f
    }
}