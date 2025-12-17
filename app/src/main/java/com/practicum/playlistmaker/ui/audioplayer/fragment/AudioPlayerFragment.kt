package com.practicum.playlistmaker.ui.audioplayer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.practicum.playlistmaker.ui.audioplayer.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.ui.audioplayer.view_model.PlayerState
import com.practicum.playlistmaker.ui.common.BindingFragment
import com.practicum.playlistmaker.ui.models.TrackParcelable
import com.practicum.playlistmaker.utils.dpToPx
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AudioPlayerFragment : BindingFragment<FragmentAudioPlayerBinding>() {
    private var track: TrackParcelable? = null
    override val applyBottomInset = true

    private val albumCornerRadiusDp: Float = ALBUM_CORNER_RADIUS_DP
    private val viewModel: AudioPlayerViewModel by viewModel { parametersOf(track?.previewUrl) }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAudioPlayerBinding {
        return FragmentAudioPlayerBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        track = arguments?.getParcelable(ARG_TRACK)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val albumCornerRadiusPx = requireContext().dpToPx(albumCornerRadiusDp)

        track?.let {
            bindData(it, albumCornerRadiusPx)
        }

        binding.btnPlay.setOnClickListener {
            viewModel.onPlayClicked()
        }

        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
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
        const val ARG_TRACK = "TRACK"
        private const val ALBUM_CORNER_RADIUS_DP = 8f

        fun createArgs(track: TrackParcelable): Bundle =
            bundleOf(ARG_TRACK to track)

    }
}