package com.practicum.playlistmaker.data.storage

import android.net.Uri
import com.practicum.playlistmaker.domain.api.image_storage.ImageStorageRepository
import java.io.File

class ImageStorageRepositoryImpl(private val imageStorage: ImageStorage) : ImageStorageRepository {
    override suspend fun saveImageToPrivateStorage(
        uri: Uri,
        playlistName: String,
    ): File? {
        return imageStorage.saveImageToPrivateStorage(
            uri,
            playlistName
        )
    }
}