package com.practicum.playlistmaker.ui.playlist.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.ui.audioplayer.fragment.AudioPlayerFragment
import com.practicum.playlistmaker.ui.common.BindingFragment
import com.practicum.playlistmaker.ui.common.controller.BottomSheetController
import com.practicum.playlistmaker.ui.mappers.toParcelable
import com.practicum.playlistmaker.ui.playlist.adapter.TracksBottomSheetAdapter
import com.practicum.playlistmaker.ui.playlist.view_model.PlaylistState
import com.practicum.playlistmaker.ui.playlist.view_model.PlaylistTracksState
import com.practicum.playlistmaker.ui.playlist.view_model.PlaylistViewModel
import com.practicum.playlistmaker.ui.playlist_editing.fragment.PlaylistEditingFragment
import com.practicum.playlistmaker.utils.dpToPx
import com.practicum.playlistmaker.utils.loadImage
import com.practicum.playlistmaker.utils.showDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistFragment : BindingFragment<FragmentPlaylistBinding>() {
    private val playlistId: Int by lazy {
        requireArguments().getInt(ARG_PLAYLIST_ID)
    }
    private val viewModel: PlaylistViewModel by viewModel { parametersOf(playlistId) }
    private lateinit var adapter: TracksBottomSheetAdapter
    private lateinit var lastTracksState: PlaylistTracksState
    private var playlistName: String = ""
    private lateinit var bottomSheetOptionsBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetTracksBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetTracksController: BottomSheetController
    private val navController by lazy { findNavController() }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistBinding {
        return FragmentPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBottomSheets()
        initRecycler()
        initListeners()
        observeViewModel()
        viewModel.initPlaylist()
    }

    private fun initFragmentData(playlistState: PlaylistState) {
        bindPlaylist(playlistState.playlist)
        bindTracks(playlistState.tracksState)
        bindDuration(playlistState.duration)
        bindPlaylistToBottomSheet(playlistState.playlist)
        setBottomSheetTracksHeight()
        playlistName = playlistState.playlist?.playlistName ?: ""
    }

    private fun initBottomSheets() {
        bottomSheetTracksBehavior = BottomSheetBehavior.from(binding.bottomSheetTracks)
        bottomSheetTracksBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        bottomSheetOptionsBehavior = BottomSheetBehavior.from(binding.bottomSheetOptions)
        bottomSheetTracksController =
            BottomSheetController(bottomSheetOptionsBehavior, binding.dimView)
        bottomSheetTracksController.init()
    }

    private fun initRecycler() {
        adapter = TracksBottomSheetAdapter(
            onLongClick = { track ->
                showDeleteTrackDialog(track.trackId)
            }, onItemClick = { track ->
                navController.navigate(
                    R.id.action_playlistFragment_to_audioPlayerFragment,
                    AudioPlayerFragment.createArgs(track.toParcelable())
                )
            })
        binding.rvTracks.adapter = adapter
        binding.rvTracks.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initListeners() {
        binding.btnBack.setOnClickListener {
            navController.navigateUp()
        }

        binding.btnShare.setOnClickListener {
            onShareClicked()
        }

        binding.btnOptions.setOnClickListener {
            viewModel.openBottomSheet()
        }

        binding.tvOptionShare.setOnClickListener {
            onShareClicked()
        }

        binding.tvEditInfo.setOnClickListener {
            viewModel.hideBottomSheet()
            navController.navigate(
                R.id.action_playlistFragment_to_playlistEditingFragment,
                PlaylistEditingFragment.createArgs(playlistId)
            )
        }

        binding.tvOptionDelete.setOnClickListener {
            viewModel.hideBottomSheet()
            showDeletePlaylistDialog()
        }

        binding.dimView.setOnClickListener {
            viewModel.hideBottomSheet()
        }
    }

    private fun observeViewModel() {
        viewModel.observePlaylistState().observe(viewLifecycleOwner) {
            initFragmentData(it)
            lastTracksState = it.tracksState
        }

        viewModel.observeDeletePlaylistFlag().observe(viewLifecycleOwner) {
            if (it) {
                navController.navigateUp()
            }
        }

        viewModel.observeBottomSheetState().observe(viewLifecycleOwner) {
            bottomSheetTracksController.toggleBottomSheet(it)
        }
    }

    private fun bindPlaylist(playlist: Playlist?) {
        playlist?.let { playlist ->
            binding.apply {
                tvPlaylistName.text = playlist.playlistName
                if (playlist.playlistDescription.isNullOrEmpty()) {
                    tvPlaylistDescription.isVisible = false
                } else {
                    tvPlaylistDescription.isVisible = true
                    tvPlaylistDescription.text = playlist.playlistDescription
                }

                tvTracksAmount.text = resources.getQuantityString(
                    R.plurals.track_count,
                    playlist.tracksAmount
                ).format(playlist.tracksAmount)

                ivPlaylistCover.loadImage(playlist.coverFilePath)
            }

        }
    }


    private fun setBottomSheetTracksHeight() {
        binding.root.doOnLayout {
            val top = binding.clPlaylist.bottom
            val maxHeight = (BOTTOM_SHEETS_MAX_HEIGHT * binding.root.height).toInt()

            bottomSheetTracksBehavior.peekHeight =
                binding.root.height - top - requireContext().dpToPx(24f)
            bottomSheetTracksBehavior.maxHeight = maxHeight
            bottomSheetOptionsBehavior.maxHeight = maxHeight
        }
    }

    private fun bindTracks(tracksState: PlaylistTracksState) {
        when (tracksState) {
            is PlaylistTracksState.Content -> {
                binding.rvTracks.isVisible = true
                binding.tvPlaceholderText.isVisible = false
                adapter.submitList(tracksState.tracks)
            }

            PlaylistTracksState.Empty -> {
                binding.rvTracks.isVisible = false
                binding.tvPlaceholderText.isVisible = true
            }
        }
    }

    private fun bindDuration(duration: Int) {
        binding.tvDuration.text =
            resources.getQuantityString(R.plurals.minutes_count, duration)
                .format(duration)
    }

    private fun bindPlaylistToBottomSheet(playlist: Playlist?) {
        playlist?.let { playlist ->
            val cornerRadPx = requireContext().dpToPx(PLAYLIST_COVER_CORNER_RADIUS_MINI)
            binding.apply {
                tvPlaylistNameMini.text = playlist.playlistName

                tvPlaylistDescriptionMini.text = resources.getQuantityString(
                    R.plurals.track_count,
                    playlist.tracksAmount
                ).format(playlist.tracksAmount)

                ivPlaylistCoverMini.loadImage(playlist.coverFilePath, cornerRadPx)
            }
        }
    }

    private fun showDeleteTrackDialog(trackId: String) {
        requireContext().showDialog(
            titleRes = R.string.wish_to_delete_track,
            messageText = "",
            positiveBtnRes = R.string.yes,
            negativeBtnRes = R.string.no,
            positiveAction = { viewModel.deleteTrack(trackId) },
        )
    }

    private fun showNoTracksDialog() {
        requireContext().showDialog(
            titleRes = R.string.empty_playlist,
            messageRes = R.string.no_tracks_to_share,
            neutralBtnRes = R.string.ok,
        )
    }

    private fun showDeletePlaylistDialog() {
        requireContext().showDialog(
            titleRes = R.string.delete_playlist,
            messageRes = R.string.sure_delete_playlist,
            neutralBtnRes = R.string.quit,
            positiveBtnRes = R.string.delete,
            positiveAction = { viewModel.deletePlaylist() },
        )
    }

    private fun onShareClicked() {
        viewModel.hideBottomSheet()
        when (lastTracksState) {
            is PlaylistTracksState.Content -> viewModel.sharePlaylist()
            PlaylistTracksState.Empty -> showNoTracksDialog()
        }
    }

    companion object {
        private const val ARG_PLAYLIST_ID = "PLAYLIST_ID"
        private const val PLAYLIST_COVER_CORNER_RADIUS_MINI = 2f
        private const val BOTTOM_SHEETS_MAX_HEIGHT = 0.8
        fun createArgs(playlistId: Int): Bundle {
            return bundleOf(ARG_PLAYLIST_ID to playlistId)
        }
    }
}