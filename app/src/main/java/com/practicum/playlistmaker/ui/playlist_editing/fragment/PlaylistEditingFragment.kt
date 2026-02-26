package com.practicum.playlistmaker.ui.playlist_editing.fragment

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.ui.playlist_creation.fragment.PlaylistCreationFragment
import com.practicum.playlistmaker.ui.playlist_editing.view_model.PlaylistEditingViewModel
import com.practicum.playlistmaker.utils.dpToPx
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class PlaylistEditingFragment : PlaylistCreationFragment() {
    private val playlistId: Int by lazy {
        requireArguments().getInt(PLAYLIST_ID_ARG)
    }
    private lateinit var playlist: Playlist
    private val viewModel: PlaylistEditingViewModel by viewModel { parametersOf(playlistId) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.playlistLiveData.observe(viewLifecycleOwner) {
            bindPlaylist(it)
            playlist = it
            it.coverFilePath?.let{ path ->
                coverUriSelected = path.toUri()
                coverSaved = File(path)
            }
        }
    }

    override fun initFragment() {
        binding.btnBack.text = requireContext().getString(R.string.back)
        binding.buttonCreate.text = requireContext().getString(R.string.save)
    }

    override fun handleBackAction() {
        findNavController().navigateUp()
    }

    private fun bindPlaylist(playlist: Playlist?) {
        val cornerRadPx = requireContext().dpToPx(PLAYLIST_CORNER_RADIUS)
        playlist?.let { playlist ->

            binding.apply {
                etPlaylistName.setText(playlist.playlistName)
                etPlaylistDescription.setText(playlist.playlistDescription ?: "")

                Glide.with(playlistCover).load(playlist.coverFilePath)
                    .placeholder(R.drawable.ic_placeholder_45).transform(
                        CenterCrop(), RoundedCorners(cornerRadPx)
                    ).into(playlistCover)
            }
        }
    }

    override fun createPlaylistObj(): Playlist {
        return playlist.copy(
            playlistName = playlistName,
            playlistDescription = playlistDescription,
            coverFilePath = coverSaved?.toUri().toString(),
        )
    }

    override fun onPlaylistUpdated(state: Boolean) {
        if (state) {
            findNavController().navigateUp()
        }
    }

    companion object {
        private const val PLAYLIST_ID_ARG = "PLAYLIST_ID_ARG"
        fun createArgs(playlistId: Int): Bundle {
            return bundleOf(PLAYLIST_ID_ARG to playlistId)
        }
    }
}