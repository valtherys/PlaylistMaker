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

open class PlaylistCreationViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val imageStorageInteractor: ImageStorageInteractor
) : ViewModel() {
    protected val playlistUpdateSuccessful = MutableLiveData(false)
    fun observePlaylistUpdated(): LiveData<Boolean> = playlistUpdateSuccessful
    protected val savedCover: MutableLiveData<File?> = MutableLiveData(null)
    fun observeSavedCover() = savedCover

    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistUpdateSuccessful.value = playlistsInteractor.addPlaylistToDb(playlist)
        }
    }

    fun saveImageIntoStorage(uri: Uri, playlistName: String) {
        viewModelScope.launch {
            val coverFile = imageStorageInteractor.saveImageToPrivateStorage(uri, playlistName)
            savedCover.value = coverFile
        }
    }
}