package com.practicum.playlistmaker.presentation.ui.search

import com.practicum.playlistmaker.domain.models.Track

interface SearchView {
    fun showLoader()
    fun hideLoader()
    fun showFoundTracks(foundTracks: ArrayList<Track>)
    fun showEmptyState()
    fun showErrorState(errorMessage: String, isConnectionError: Boolean)
    fun clearTracks()
}