package com.practicum.playlistmaker.domain.api.sharing

import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track

interface SharingInteractor {
    fun shareApp()
    fun openTerms()
    fun openSupport()
    fun sharePlaylist(
        tracksAmountString: String,
        playlist: Playlist,
        tracks: List<Track>
    )
}