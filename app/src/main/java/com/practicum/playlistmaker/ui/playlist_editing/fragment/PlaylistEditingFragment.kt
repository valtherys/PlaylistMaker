package com.practicum.playlistmaker.ui.playlist_editing.fragment

import android.os.Bundle
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.ui.playlist_creation.fragment.PlaylistCreationFragment
import com.practicum.playlistmaker.ui.playlist_editing.view_model.PlaylistEditingViewModel
import com.practicum.playlistmaker.utils.loadImage
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class PlaylistEditingFragment : PlaylistCreationFragment() {
    private val playlistId: Int by lazy {
        requireArguments().getInt(PLAYLIST_ID_ARG)
    }
    private lateinit var playlist: Playlist
    private val viewModel: PlaylistEditingViewModel by viewModel { parametersOf(playlistId) }

    override fun observeViewModel() {
        super.observeViewModel()

        viewModel.playlistLiveData.observe(viewLifecycleOwner) {
            bindPlaylist(it)
            playlist = it
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
        playlist?.let { playlist ->

            binding.apply {
                etPlaylistName.setText(playlist.playlistName)
                etPlaylistDescription.setText(playlist.playlistDescription ?: "")
                binding.playlistCover.loadImage(playlist.coverFilePath, playlistCornerRadiusPx)
            }
        }
    }

    override fun createPlaylistObj(): Playlist {
        return playlist.copy(
            playlistName = playlistName,
            playlistDescription = playlistDescription,
            coverFilePath = coverSaved?.toUri()?.toString() ?: playlist.coverFilePath,
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