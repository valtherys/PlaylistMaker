package com.practicum.playlistmaker.presentation.ui.audioplayer

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.practicum.playlistmaker.presentation.ui.models.TrackParcelable
import com.practicum.playlistmaker.utils.applySystemBarsPadding
import com.practicum.playlistmaker.utils.dpToPx
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity(),  AudioPlayerView {
    private lateinit var binding: ActivityAudioPlayerBinding
    private var track: TrackParcelable? = null
    private val albumCornerRadiusDp: Float = ALBUM_CORNER_RADIUS_DP
    private val audioPlayerPresenter = Creator.provideAudioPlayerPresenter()
    private val dateFormat by lazy {
        SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        )
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

        audioPlayerPresenter.onPlayerPrepare(track?.previewUrl)

        bindData(track!!, albumCornerRadiusPx)

        binding.btnPlay.setOnClickListener {
            audioPlayerPresenter.onPlayerPlaybackControl()
        }

        binding.btnBack.setOnClickListener { finish() }
    }

    override fun onStart() {
        super.onStart()
        audioPlayerPresenter.attach(this)
    }

    override fun onPause() {
        super.onPause()
        audioPlayerPresenter.onPause()
    }

    override fun onStop() {
        super.onStop()
        audioPlayerPresenter.detach()
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayerPresenter.onReleaseResources()
    }

    private fun bindData(track: TrackParcelable, cornerRadiusPx: Int) {
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

    override fun onPlayerStart() {
        runOnUiThread {
            binding.btnPlay.setImageResource(R.drawable.ic_pause_100)
        }
    }

    override fun onPlayerChangePosition(position: Int) {
        runOnUiThread {
        binding.tvTimer.text = dateFormat.format(position)}
    }

    override fun onPlayerPause() {
        runOnUiThread {
            binding.btnPlay.setImageResource(R.drawable.ic_play_100)
        }
    }

    override fun onPlayerPrepared() {
        runOnUiThread {
            binding.btnPlay.isEnabled = true
        }
    }

    override fun onPlayerCompletion() {
        runOnUiThread {
            binding.btnPlay.setImageResource(R.drawable.ic_play_100)
            binding.tvTimer.text = getString(R.string.count_start)
        }
    }

    companion object {
        const val INTENT_EXTRA_KEY = "TRACK"
        private const val ACTIVITY_TAG = "AudioPlayerActivity"
        private const val ALBUM_CORNER_RADIUS_DP = 8f
    }
}