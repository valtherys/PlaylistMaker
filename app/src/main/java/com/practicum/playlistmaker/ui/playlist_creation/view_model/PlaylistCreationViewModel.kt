package com.practicum.playlistmaker.ui.playlist_creation.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.api.db.PlaylistsInteractor
import com.practicum.playlistmaker.domain.api.image_storage.ImageStorageInteractor
import com.practicum.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.launch
import java.io.File

class PlaylistCreationViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val imageStorageInteractor: ImageStorageInteractor
) : ViewModel() {
    private val _playlistCreatedFlag: MutableLiveData<Boolean> = MutableLiveData(false)
    fun observePlaylistCreationFlag(): LiveData<Boolean> = _playlistCreatedFlag

    private val _savedCover: MutableLiveData<File?> = MutableLiveData(null)
    fun observeSavedCover() = _savedCover

    fun createPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            val addingIsSuccessful = playlistsInteractor.addPlaylistToDb(playlist)
            if (addingIsSuccessful) {
                _playlistCreatedFlag.postValue(true)
            }
        }
    }

    fun saveImageIntoStorage(uri: Uri, playlistName: String) {
        viewModelScope.launch {
            val coverFile = imageStorageInteractor.saveImageToPrivateStorage(uri, playlistName)
            _savedCover.value = coverFile
        }
    }
}