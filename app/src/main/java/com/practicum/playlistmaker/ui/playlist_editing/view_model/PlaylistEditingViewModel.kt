package com.practicum.playlistmaker.ui.playlist_editing.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.practicum.playlistmaker.domain.api.db.PlaylistsInteractor
import com.practicum.playlistmaker.domain.api.image_storage.ImageStorageInteractor
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.ui.playlist_creation.view_model.PlaylistCreationViewModel

class PlaylistEditingViewModel(
    private val playlistId: Int,
    private val playlistsInteractor: PlaylistsInteractor,
    private val imageStorageInteractor: ImageStorageInteractor,
) : PlaylistCreationViewModel(playlistsInteractor, imageStorageInteractor) {
    val playlistLiveData: LiveData<Playlist> =
        playlistsInteractor.getPlaylist(playlistId).asLiveData()
}