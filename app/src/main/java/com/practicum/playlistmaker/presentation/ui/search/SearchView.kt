package com.practicum.playlistmaker.presentation.ui.search

import com.practicum.playlistmaker.domain.models.Track

interface SearchView {
    fun showLoader()
    fun hideLoader()
    fun showFoundTracks(foundTracks: List<Track>)
    fun showEmptyState()
    fun showErrorState(errorMessage: String, isConnectionError: Boolean)
}