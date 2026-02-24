package com.practicum.playlistmaker.ui.medialibrary.playlists.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.ui.common.BindingFragment
import com.practicum.playlistmaker.ui.medialibrary.playlists.adapters.PlaylistsAdapter
import com.practicum.playlistmaker.ui.medialibrary.playlists.view_model.PlaylistsState
import com.practicum.playlistmaker.ui.medialibrary.playlists.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.ui.playlist.fragment.PlaylistFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : BindingFragment<FragmentPlaylistsBinding>() {
    private val viewmodel: PlaylistsViewModel by viewModel()
    private lateinit var playlistsAdapter: PlaylistsAdapter

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistsBinding {
        return FragmentPlaylistsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistsAdapter =
            PlaylistsAdapter { playlist ->
                findNavController().navigate(
                    R.id.action_mediaLibraryFragment_to_playlistFragment,
                    PlaylistFragment.createArgs(playlist.playlistId ?: 0)
                )
            }
        binding.playlists.adapter = playlistsAdapter
        binding.playlists.layoutManager = GridLayoutManager(requireContext(), GRID_COLUMNS_AMOUNT)

        binding.btnCreateNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_playlistCreationFragment)
        }

        viewmodel.playlistsLiveData.observe(viewLifecycleOwner) { render(it) }
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            PlaylistsState.Empty -> showPlaceholder()
            is PlaylistsState.Content -> showContent(state.playlists)
        }
    }

    private fun showPlaceholder() {
        binding.llPlaceholder.isVisible = true
        binding.playlists.isVisible = false
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.llPlaceholder.isVisible = false
        binding.playlists.isVisible = true

        playlistsAdapter.submitList(playlists)
    }

    companion object {
        fun newInstance() = PlaylistsFragment().apply { }

        private const val GRID_COLUMNS_AMOUNT = 2
    }
}