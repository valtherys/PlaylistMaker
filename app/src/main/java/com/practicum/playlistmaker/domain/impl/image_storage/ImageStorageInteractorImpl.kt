package com.practicum.playlistmaker.domain.impl.image_storage

import android.net.Uri
import com.practicum.playlistmaker.domain.api.image_storage.ImageStorageInteractor
import com.practicum.playlistmaker.domain.api.image_storage.ImageStorageRepository

class ImageStorageInteractorImpl(private val repository: ImageStorageRepository) :
    ImageStorageInteractor {
    override suspend fun saveImageToPrivateStorage(
        uri: Uri,
        playlistName: String
    ): Uri? {
        return repository.saveImageToPrivateStorage(uri, playlistName)
    }
}