package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.ui.audioplayer.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.ui.medialibrary.favorite_tracks.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.ui.medialibrary.playlists.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.ui.medialibrary.view_model.MediaLibraryViewModel
import com.practicum.playlistmaker.ui.models.TrackParcelable
import com.practicum.playlistmaker.ui.playlist.view_model.PlaylistViewModel
import com.practicum.playlistmaker.ui.playlist_creation.view_model.PlaylistCreationViewModel
import com.practicum.playlistmaker.ui.search.view_model.TracksViewModel
import com.practicum.playlistmaker.ui.settings.view_model.UserSettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { (track: TrackParcelable) ->
        AudioPlayerViewModel(
            get(),
            get(),
            get(),
            get(TRACK_TIME_CLIENT),
            track
        )
    }
    viewModel {
        TracksViewModel(get(), get(), get())
    }
    viewModel { UserSettingsViewModel(get(), get()) }

    viewModel { MediaLibraryViewModel() }
    viewModel { PlaylistsViewModel(get()) }
    viewModel { FavoriteTracksViewModel(get()) }
    viewModel { PlaylistCreationViewModel(get(), get()) }
    viewModel { (playlistId: Int) ->
        PlaylistViewModel(
            get(),
            get(),
            playlistId
        )
    }
}