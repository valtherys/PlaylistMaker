package com.practicum.playlistmaker.domain.api.image_storage

import android.net.Uri

interface ImageStorageInteractor {
    suspend fun saveImageToPrivateStorage(
        uri: Uri,
        playlistName: String,
    ): Uri?
}