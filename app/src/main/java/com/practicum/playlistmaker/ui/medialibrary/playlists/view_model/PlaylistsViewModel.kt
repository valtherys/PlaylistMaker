package com.practicum.playlistmaker.ui.medialibrary.playlists.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.practicum.playlistmaker.domain.api.db.PlaylistsInteractor
import kotlinx.coroutines.flow.map

class PlaylistsViewModel(private val interactor: PlaylistsInteractor) : ViewModel() {
    val playlistsLiveData:LiveData<PlaylistsState> = interactor.getPlaylistsFromDb().map { playlists ->
        if (playlists.isNullOrEmpty()) {
          PlaylistsState.Empty
        } else {
           PlaylistsState.Content(playlists)
        }
    }.asLiveData()
}