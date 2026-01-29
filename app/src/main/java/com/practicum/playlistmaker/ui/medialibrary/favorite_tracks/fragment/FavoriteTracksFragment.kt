package com.practicum.playlistmaker.ui.medialibrary.favorite_tracks.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSelectedTracksBinding
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.audioplayer.fragment.AudioPlayerFragment
import com.practicum.playlistmaker.ui.common.BindingFragment
import com.practicum.playlistmaker.ui.common.adapters.TracksAdapter
import com.practicum.playlistmaker.ui.mappers.toParcelableFavorite
import com.practicum.playlistmaker.ui.medialibrary.favorite_tracks.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.ui.medialibrary.favorite_tracks.view_model.FavoritesState
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : BindingFragment<FragmentSelectedTracksBinding>() {
    private val viewModel: FavoriteTracksViewModel by viewModel()
    private lateinit var adapter: TracksAdapter

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSelectedTracksBinding {
        return FragmentSelectedTracksBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter =
            TracksAdapter { track ->
                findNavController().navigate(
                    R.id.action_mediaLibraryFragment_to_audioPlayerFragment,
                    AudioPlayerFragment.createArgs(track.toParcelableFavorite())
                )
            }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.observeFavorites().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun showEmptyState(message: String) {
        binding.llPlaceholder.isVisible = true
        binding.recyclerView.isVisible = false
        binding.tvComment.text = message
    }

    private fun showContent(tracks: List<Track>) {
        binding.llPlaceholder.isVisible = false
        binding.recyclerView.isVisible = true
        adapter.submitList(tracks)
    }


    private fun render(state: FavoritesState) {
        when (state) {
            is FavoritesState.Empty -> showEmptyState(state.message)
            is FavoritesState.Content -> showContent(state.favoriteTracks)
        }
    }

    companion object {
        fun newInstance() = FavoriteTracksFragment().apply { }
    }
}