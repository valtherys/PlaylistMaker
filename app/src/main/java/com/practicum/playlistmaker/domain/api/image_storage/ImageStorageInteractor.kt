package com.practicum.playlistmaker.domain.api.image_storage

import android.net.Uri
import java.io.File

interface ImageStorageInteractor {
    suspend fun saveImageToPrivateStorage(
        uri: Uri,
        playlistName: String,
    ): File?
}