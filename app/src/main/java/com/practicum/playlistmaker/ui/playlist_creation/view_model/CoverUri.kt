package com.practicum.playlistmaker.ui.playlist_creation.view_model

import android.net.Uri

sealed interface CoverUri {
    object Uninitialized : CoverUri
    data class CoverSelected(var uri: Uri) : CoverUri
    data class CoverSaved(var uri: Uri?) : CoverUri
}