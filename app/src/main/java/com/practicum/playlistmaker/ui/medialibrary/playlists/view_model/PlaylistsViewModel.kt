package com.practicum.playlistmaker.ui.medialibrary.playlists.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.api.db.PlaylistsInteractor
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val interactor: PlaylistsInteractor) : ViewModel() {
    private val _playlistsLiveData: MutableLiveData<PlaylistsState> = MutableLiveData()
    fun observePlaylists(): LiveData<PlaylistsState> = _playlistsLiveData

//    init {
//        viewModelScope.launch { interactor.deletePlaylists() }
//    }

    fun onGetPlaylistsFromDb() {
        viewModelScope.launch {
            interactor.getPlaylistsFromDb().collect { playlists ->
                if (playlists.isNullOrEmpty()) {
                    _playlistsLiveData.postValue(PlaylistsState.Empty)
                } else {
                    _playlistsLiveData.postValue(PlaylistsState.Content(playlists))
                }
            }
        }
    }
}