package com.practicum.playlistmaker.domain.api.sharing

interface SharingInteractor {
    fun shareApp()
    fun openTerms()
    fun openSupport()
    suspend fun sharePlaylist(playlistId: Int, trackIds: List<String>)
}