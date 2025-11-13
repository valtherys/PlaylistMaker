package com.practicum.playlistmaker.ui.search.view_model

import com.practicum.playlistmaker.domain.models.Track

sealed interface SearchState{
    object Loading: SearchState
    data class Empty(val message: String): SearchState
    data class Content(val tracks: List<Track>): SearchState
    data class Error(val message: String): SearchState
    data class Connection(val message: String): SearchState
}