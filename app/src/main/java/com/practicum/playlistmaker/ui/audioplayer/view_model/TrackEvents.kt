package com.practicum.playlistmaker.ui.audioplayer.view_model

import androidx.annotation.StringRes

sealed interface TrackEvents{
    data class TrackAdded(val playlistName: String, @StringRes val message: Int) : TrackEvents
    data class TrackAlreadyAdded(val playlistName: String, @StringRes val message: Int) : TrackEvents

}