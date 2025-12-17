package com.practicum.playlistmaker.ui.search.view_model

import com.practicum.playlistmaker.domain.models.Track

sealed interface TracksState{
    object Loading: TracksState
    data class Empty(val message: String): TracksState
    data class SearchContent(val tracks: List<Track>): TracksState
    data class Error(val message: String): TracksState
    data class Connection(val message: String): TracksState
    object HiddenHistory: TracksState
    data class HistoryContent(val tracks: List<Track>): TracksState
}