package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.ui.audioplayer.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.ui.medialibrary.playlists.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.ui.medialibrary.selected_tracks.view_model.SelectedTracksViewModel
import com.practicum.playlistmaker.ui.medialibrary.view_model.MediaLibraryViewModel
import com.practicum.playlistmaker.ui.search.view_model.TracksViewModel
import com.practicum.playlistmaker.ui.settings.view_model.UserSettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { (previewUrl: String) ->
        AudioPlayerViewModel(
            get(),
            get(TRACK_TIME_CLIENT),
            previewUrl
        )
    }
    viewModel {
        TracksViewModel(get(), get(), get())
    }
    viewModel { UserSettingsViewModel(get(), get()) }

    viewModel { MediaLibraryViewModel() }
    viewModel { PlaylistsViewModel() }
    viewModel { SelectedTracksViewModel() }
}