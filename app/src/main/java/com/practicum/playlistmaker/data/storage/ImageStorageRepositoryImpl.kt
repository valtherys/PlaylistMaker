package com.practicum.playlistmaker.data.storage

import android.net.Uri
import androidx.core.net.toUri
import com.practicum.playlistmaker.domain.api.image_storage.ImageStorageRepository

class ImageStorageRepositoryImpl(private val imageStorage: ImageStorage) : ImageStorageRepository {
    override suspend fun saveImageToPrivateStorage(
        uri: Uri,
        playlistName: String,
    ): Uri? {
        return imageStorage.saveImageToPrivateStorage(
            uri,
            playlistName
        )?.toUri()
    }
}