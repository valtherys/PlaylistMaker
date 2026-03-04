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

open class PlaylistCreationViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val imageStorageInteractor: ImageStorageInteractor
) : ViewModel() {
    protected val playlistUpdateSuccessful = MutableLiveData(false)
    fun observePlaylistUpdated(): LiveData<Boolean> = playlistUpdateSuccessful

    protected val coverUri = MutableLiveData<CoverUri>(CoverUri.Uninitialized)

    fun observeCoverUri(): LiveData<CoverUri> = coverUri
    val cover = coverUri

    fun onCoverSelected(uri: Uri) {
        coverUri.value = CoverUri.CoverSelected(uri)
    }

    fun onSaveImageIntoStorage(playlistName: String) {
        when (val current = coverUri.value) {
            is CoverUri.CoverSelected -> {
                viewModelScope.launch {
                    val coverFile =
                        imageStorageInteractor.saveImageToPrivateStorage(current.uri, playlistName)
                    coverFile?.let {
                        coverUri.value = CoverUri.CoverSaved(it)
                    }
                }
            }

            else -> coverUri.value = CoverUri.CoverSaved(null)
        }
    }

    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistUpdateSuccessful.value = playlistsInteractor.addPlaylistToDb(playlist)
        }
    }
}