package com.practicum.playlistmaker.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.model.Track
import com.practicum.playlistmaker.utils.applySystemBarsPadding
import com.practicum.playlistmaker.utils.dpToPx
import androidx.constraintlayout.widget.Group

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    private lateinit var albumCover: ImageView
    private lateinit var trackName: TextView
    private lateinit var trackArtist: TextView
    private lateinit var trackDuration: TextView
    private lateinit var trackAlbum: TextView
    private lateinit var trackReleaseYear: TextView
    private lateinit var trackGenre: TextView
    private lateinit var trackCountry: TextView
    private lateinit var albumGroup: Group
    private lateinit var yearGroup: Group
    private var track: Track? = null

    private val albumCornerRadiusDp: Float = ALBUM_CORNER_RADIUS_DP.toFloat()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)
        val root = findViewById<ConstraintLayout>(R.id.main)
        root.applySystemBarsPadding()

        track = intent.getParcelableExtra(
            INTENT_EXTRA_KEY
        )
        if (track == null) {
            Log.e(ACTIVITY_TAG, "No track passed to activity")
            finish()
            return
        }
        btnBack = findViewById(R.id.btn_back)
        albumCover = findViewById(R.id.iv_album_cover)
        trackName = findViewById(R.id.tv_track_name)
        trackArtist = findViewById(R.id.tv_track_artist)
        trackDuration = findViewById(R.id.tv_duration_data)
        trackAlbum = findViewById(R.id.tv_album_data)
        trackReleaseYear = findViewById(R.id.tv_year_data)
        trackGenre = findViewById(R.id.tv_genre_data)
        trackCountry = findViewById(R.id.tv_country_data)
        albumGroup = findViewById(R.id.group_album)
        yearGroup = findViewById(R.id.group_year)

        val albumCornerRadiusPx = dpToPx(albumCornerRadiusDp)

        bindData(track!!, albumCornerRadiusPx)
        btnBack.setOnClickListener { finish() }
    }

    private fun bindData(track: Track, cornerRadiusPx: Int) {
        val albumCoverHighResolution = track.getArtworkUrlHighResolution()
        Glide.with(this)
            .load(albumCoverHighResolution)
            .placeholder(R.drawable.ic_placeholder_45)
            .transform(RoundedCorners(cornerRadiusPx)).into(albumCover)

        trackName.text = track.trackName
        trackArtist.text = track.artistName
        trackDuration.text = track.trackTime
        trackGenre.text = track.primaryGenreName
        trackCountry.text = track.country

        hideViewGroupIfEmpty(track.collectionName, albumGroup, trackAlbum)
        hideViewGroupIfEmpty(track.getReleaseYear(), yearGroup, trackReleaseYear)
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