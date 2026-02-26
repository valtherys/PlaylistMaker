package com.practicum.playlistmaker.ui.playlist.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getColor
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.audioplayer.fragment.AudioPlayerFragment
import com.practicum.playlistmaker.ui.common.BindingFragment
import com.practicum.playlistmaker.ui.common.controller.BottomSheetController
import com.practicum.playlistmaker.ui.mappers.toParcelable
import com.practicum.playlistmaker.ui.playlist.adapter.TracksBottomSheetAdapter
import com.practicum.playlistmaker.ui.playlist.view_model.PlaylistState
import com.practicum.playlistmaker.ui.playlist.view_model.PlaylistTracksState
import com.practicum.playlistmaker.ui.playlist.view_model.PlaylistViewModel
import com.practicum.playlistmaker.utils.dpToPx
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistFragment : BindingFragment<FragmentPlaylistBinding>() {
    override val applyBottomInset = true
    private val playlistId: Int by lazy {
        requireArguments().getInt(ARG_PLAYLIST_ID)
    }
    private val cornerRadPx: Float by lazy {
        requireContext().dpToPx(PLAYLIST_COVER_CORNER_RADIUS).toFloat()
    }
    private val viewModel: PlaylistViewModel by viewModel { parametersOf(playlistId) }
    private lateinit var adapter: TracksBottomSheetAdapter
    private var noTracksFlag = false
    private var playlistName: String = ""
    private lateinit var bottomSheetOptionsBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetController: BottomSheetController

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistBinding {
        return FragmentPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheetTracksBehavior = BottomSheetBehavior.from(binding.bottomSheetTracks)
        bottomSheetTracksBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        bottomSheetOptionsBehavior = BottomSheetBehavior.from(binding.bottomSheetOptions)
        bottomSheetController = BottomSheetController(bottomSheetOptionsBehavior, binding.dimView)
        bottomSheetController.init()

        adapter = TracksBottomSheetAdapter(
            onLongClick = { track ->
                showDeleteTrackDialog(track)
            }, onItemClick = { track ->
                findNavController().navigate(
                    R.id.action_playlistFragment_to_audioPlayerFragment,
                    AudioPlayerFragment.createArgs(track.toParcelable())
                )
            })
        binding.rvTracks.adapter = adapter
        binding.rvTracks.layoutManager = LinearLayoutManager(requireContext())

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
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

        binding.tvOptionDelete.setOnClickListener {
            viewModel.hideBottomSheet()
            showDeletePlaylistDialog()
        }

        binding.dimView.setOnClickListener {
            viewModel.hideBottomSheet()
        }

        viewModel.observePlaylistState().observe(viewLifecycleOwner) {
            bindData(it)
            noTracksFlag = it.playlist?.tracksAmount == 0
        }

        viewModel.observeDeletePlaylistFlag().observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigateUp()
            }
        }

        viewModel.observeBottomSheetState().observe(viewLifecycleOwner) {
            bottomSheetController.toggleBottomSheet(it)
        }

        viewModel.initPlaylist()
    }

    private fun bindData(playlistState: PlaylistState) {
        bindPlaylist(playlistState.playlist)
        bindTracks(playlistState.tracksState)
        bindDuration(playlistState.duration)
        bindPlaylistToBottomSheet(playlistState.playlist)
        playlistName = playlistState.playlist?.playlistName ?: ""
    }

    private fun bindPlaylist(playlist: Playlist?) {
        playlist?.let { playlist ->
            binding.apply {
                tvPlaylistName.text = playlist.playlistName
                if (playlist.playlistDescription.isNullOrEmpty()) {
                    tvPlaylistDescription.isVisible = false
                } else tvPlaylistDescription.text = playlist.playlistDescription


                tvTracksAmount.text = requireContext().resources.getQuantityString(
                    R.plurals.track_count,
                    playlist.tracksAmount
                ).format(playlist.tracksAmount)


                Glide.with(ivPlaylistCover).load(playlist.coverFilePath)
                    .placeholder(R.drawable.ic_placeholder_45).transform(
                        CenterCrop(),
                        GranularRoundedCorners(cornerRadPx, cornerRadPx, 0f, 0f)
                    ).into(ivPlaylistCover)
            }

        }
    }

    private fun bindTracks(tracksState: PlaylistTracksState) {
        when (tracksState) {
            is PlaylistTracksState.Content -> {
                binding.bottomSheetTracks.isVisible = true
                adapter.submitList(tracksState.tracks)

            }

            PlaylistTracksState.Empty -> {
                binding.bottomSheetTracks.isVisible = false
            }
        }
    }

    private fun buildDeletePlaylistDialog(): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(
                requireContext().resources.getString(R.string.delete_named_playlist)
                    .format(playlistName)
            )
            .setMessage("")
            .setNegativeButton(R.string.no) { _, _ ->
            }.setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deletePlaylist()
            }
    }

    private fun bindDuration(duration: Int) {
        binding.tvDuration.text =
            requireContext().resources.getQuantityString(R.plurals.minutes_count, duration)
                .format(duration)
    }

    private fun bindPlaylistToBottomSheet(playlist: Playlist?) {
        playlist?.let { playlist ->
            val cornerRadPx = requireContext().dpToPx(PLAYLIST_COVER_CORNER_RADIUS_MINI)
            binding.apply {
                tvPlaylistNameMini.text = playlist.playlistName

                tvPlaylistDescriptionMini.text = requireContext().resources.getQuantityString(
                    R.plurals.track_count,
                    playlist.tracksAmount
                ).format(playlist.tracksAmount)


                Glide.with(ivPlaylistCoverMini).load(playlist.coverFilePath)
                    .placeholder(R.drawable.ic_placeholder_45).transform(
                        CenterCrop(), RoundedCorners(cornerRadPx)
                    )
                    .into(ivPlaylistCoverMini)
            }
        }
    }

    private fun showDeleteTrackDialog(track: Track) {
        val deleteTrackDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.wish_to_delete_track)
            .setMessage("")
            .setNegativeButton(R.string.no) { _, _ ->
            }.setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deleteTrack(track)
            }

        val dialog = deleteTrackDialog.show()
        stileDialog(dialog)
    }

    private fun showNoTracksDialog() {
        val noTracksDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.empty_playlist)
            .setMessage(R.string.no_tracks_to_share)
            .setNeutralButton(R.string.ok) { _, _ -> }

        val dialog = noTracksDialog.show()
        stileDialog(dialog)
    }

    private fun showDeletePlaylistDialog() {
        val deletePlaylistDialog = buildDeletePlaylistDialog()
        val dialog = deletePlaylistDialog.show()
        stileDialog(dialog)
    }

    private fun onShareClicked() {
        viewModel.hideBottomSheet()
        if (noTracksFlag) {
            showNoTracksDialog()
        } else {
            viewModel.sharePlaylist()
        }
    }

    private fun stileDialog(dialog: AlertDialog) {
        val buttonPadding = requireContext().dpToPx(DIALOG_BUTTON_PADDING_END)
        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(getColor(requireContext(), R.color.YP_Blue))
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(getColor(requireContext(), R.color.YP_Blue))
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
            .setTextColor(getColor(requireContext(), R.color.YP_Blue))
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setPadding(
            buttonPadding,
            DIALOG_BUTTON_PADDING_VERTICAL,
            buttonPadding,
            DIALOG_BUTTON_PADDING_VERTICAL
        )
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setPadding(
            DIALOG_BUTTON_PADDING_START,
            DIALOG_BUTTON_PADDING_VERTICAL,
            buttonPadding,
            DIALOG_BUTTON_PADDING_VERTICAL
        )
    }

    companion object {
        private const val ARG_PLAYLIST_ID = "PLAYLIST_ID"
        private const val DIALOG_BUTTON_PADDING_END = 16f
        private const val DIALOG_BUTTON_PADDING_START = 0
        private const val DIALOG_BUTTON_PADDING_VERTICAL = 0
        private const val PLAYLIST_COVER_CORNER_RADIUS = 21f
        private const val PLAYLIST_COVER_CORNER_RADIUS_MINI = 2f

        fun createArgs(playlistId: Int): Bundle {
            return bundleOf(ARG_PLAYLIST_ID to playlistId)
        }
    }
}