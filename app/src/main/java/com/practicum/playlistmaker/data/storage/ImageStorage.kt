package com.practicum.playlistmaker.data.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ImageStorage(
    private val context: Context
) {
    suspend fun saveImageToPrivateStorage(
        uri: Uri,
        playlistName: String,
    ): File? = withContext(Dispatchers.IO) {
        val filePath = File(context.filesDir, COVERS_FOLDER_NAME)

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val coverFile = File(filePath, "${playlistName}_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(coverFile).use { outputStream ->
                BitmapFactory.decodeStream(inputStream)
                    .compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, outputStream)
            }
            return@withContext coverFile
        } ?: Log.e("SaveImage", "Failed to open input stream")
        return@withContext null
    }

    companion object {
        private const val IMAGE_QUALITY = 100
        private const val COVERS_FOLDER_NAME = "playlist_covers"
    }
}