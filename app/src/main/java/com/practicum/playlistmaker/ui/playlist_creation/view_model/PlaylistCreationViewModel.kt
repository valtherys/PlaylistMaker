package com.practicum.playlistmaker.ui.playlist_creation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.api.db.PlaylistsInteractor
import com.practicum.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.launch

class PlaylistCreationViewModel(private val interactor: PlaylistsInteractor) : ViewModel() {
    private val _toastFlag: MutableLiveData<Boolean> = MutableLiveData(false)
    fun observeToastFlag(): LiveData<Boolean> = _toastFlag

    fun createPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            val playlistId = interactor.addPlaylistToDb(playlist)
            if (playlistId > 0) {
                _toastFlag.postValue(true)
            }
        }
    }
}