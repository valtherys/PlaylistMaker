package com.practicum.playlistmaker.ui.playlist_creation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.api.db.PlaylistsInteractor
import com.practicum.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.launch

class PlaylistCreationViewModel(private val interactor: PlaylistsInteractor) : ViewModel() {
    private val _playlistCreatedFlag: MutableLiveData<Boolean> = MutableLiveData(false)
    fun observePlaylistCreationFlag(): LiveData<Boolean> = _playlistCreatedFlag

    fun createPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            val addingIsSuccessful = interactor.addPlaylistToDb(playlist)
            if (addingIsSuccessful) {
                _playlistCreatedFlag.postValue(true)
            }
        }
    }
}