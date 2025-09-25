package com.practicum.playlistmaker.ui

import android.os.Bundle
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

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioPlayerBinding
    private var track: Track? = null
    private val albumCornerRadiusDp: Float = ALBUM_CORNER_RADIUS_DP.toFloat()

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
        binding.btnBack.setOnClickListener { finish() }
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

    companion object {
        const val INTENT_EXTRA_KEY = "TRACK"
        private const val ACTIVITY_TAG = "AudioPlayerActivity"
        private const val ALBUM_CORNER_RADIUS_DP = "8f"
    }
}