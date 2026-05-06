package com.practicum.playlistmaker.ui.medialibrary.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.audioplayer.fragment.AudioPlayerFragment
import com.practicum.playlistmaker.ui.mappers.toParcelableFavorite
import com.practicum.playlistmaker.ui.medialibrary.favorite_tracks.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.ui.medialibrary.playlists.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.ui.medialibrary.screen.MediaLibraryScreen
import com.practicum.playlistmaker.ui.playlist.fragment.PlaylistFragment
import com.practicum.playlistmaker.ui.theme.AppTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibraryFragment : Fragment() {
    lateinit var onTrackClick: (Track) -> Unit
    lateinit var onPlaylistClick: (Playlist) -> Unit
    lateinit var onNewPlaylistClick: () -> Unit
    val tracksViewModel: FavoriteTracksViewModel by viewModel()
    val playlistsViewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    MediaLibraryScreen(
                        onTrackClick = onTrackClick,
                        onPlaylistClick = onPlaylistClick,
                        onNewPlaylistClick = onNewPlaylistClick,
                        tracksViewModel = tracksViewModel,
                        playlistsViewModel = playlistsViewModel,
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onTrackClick = { track ->
            findNavController().navigate(
                R.id.action_mediaLibraryFragment_to_audioPlayerFragment,
                AudioPlayerFragment.createArgs(track.toParcelableFavorite())
            )
        }
        onPlaylistClick = { playlist ->
            findNavController().navigate(
                R.id.action_mediaLibraryFragment_to_playlistFragment,
                PlaylistFragment.createArgs(playlist.playlistId ?: 0)
            )
        }
        onNewPlaylistClick = {
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_playlistCreationFragment)
        }
    }
}